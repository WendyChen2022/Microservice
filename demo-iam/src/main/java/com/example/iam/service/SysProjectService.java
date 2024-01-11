package com.example.iam.service;

import com.alibaba.fastjson.JSON;
import com.example.common.constat.DemoConstant;
import com.example.common.dto.ProjectDto;
import com.example.common.model.SysProject;
import com.example.common.model.SysRoleAuth;
import com.example.common.model.SysUserProject;
import com.example.iam.dao.SysProjectMapper;
import com.example.iam.dao.SysRoleAuthMapper;
import com.example.iam.dao.SysUserProjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysProjectService {

    @Autowired
    private SysProjectMapper sysProjectMapper;

    @Autowired
    private SysUserProjectMapper sysUserProjectMapper;

    @Autowired
    private SysRoleAuthMapper sysRoleAuthMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public SysProject createProject(ProjectDto projectDto, String userId) {
        SysProject sysProject = new SysProject();
        sysProject.setId(UUID.randomUUID().toString());
        sysProject.setContent(projectDto.getContent());
        sysProject.setTitle(projectDto.getTitle());
        sysProject.setStatus(0);
        int size = projectDto.getPicUrl().size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append("https://www.amazon.org?picurl=" + UUID.randomUUID().toString().replace("-", ""));
            if (i == size - 1) {
                continue;
            }
            builder.append(",");
        }
        sysProject.setAttachment(builder.toString());
        sysProjectMapper.insert(sysProject);

        SysUserProject sysUserProject = new SysUserProject();
        sysUserProject.setId(UUID.randomUUID().toString());
        sysUserProject.setProjectId(sysProject.getId());
        sysUserProject.setUserId(userId);
        sysUserProject.setUserRole("leader");
        sysUserProjectMapper.insert(sysUserProject);
        String authRedisKey = DemoConstant.getAuthRedisKey(userId);
        stringRedisTemplate.delete(authRedisKey);

        return sysProject;
    }

    @SneakyThrows
    public SysProject readProject(String projectId, String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return null;
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        if (authMap.containsKey(projectId)) {
            SysProject sysProject = sysProjectMapper.selectByPrimaryKey(projectId);
            return sysProject;
        } else {
            throw new AuthenticationException("You do not have permission to view the project");
        }
    }

    public List<SysProject> findAll(String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return new ArrayList<>();
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        List<String> keys = new ArrayList<>(authMap.keySet());
        return keys.stream()
                .map(key -> sysProjectMapper.selectByPrimaryKey(key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public PageInfo<SysProject> findPage(int page, int size, String useId) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(findAll(useId));
    }

    @SneakyThrows
    public Boolean updateProject(SysProject sysProject, String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return false;
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        if (!authMap.containsKey(sysProject.getId())) {
            throw new AuthenticationException("You do not have permission to edit this item");
        }
        String role = authMap.get(sysProject.getId());
        Example example = new Example(SysRoleAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleName", role);
        List<SysRoleAuth> sysRoleAuths = sysRoleAuthMapper.selectByExample(example);
        List<String> sysAuthNames = sysRoleAuths.stream().map(SysRoleAuth::getAuthName).collect(Collectors.toList());
        if (sysAuthNames.contains("update")){
            return sysProjectMapper.updateByPrimaryKey(sysProject) > 0;
        }else {
            throw new AuthenticationException("You do not have permission to edit this item");
        }
    }

    @SneakyThrows
    public Boolean deleteProject(String projectId, String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return false;
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        if (!authMap.containsKey(projectId)) {
            throw new AuthenticationException("You do not have permission to delete this item");
        }
        String role = authMap.get(projectId);
        Example example = new Example(SysRoleAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleName", role);
        List<SysRoleAuth> sysRoleAuths = sysRoleAuthMapper.selectByExample(example);
        List<String> sysAuthNames = sysRoleAuths.stream().map(SysRoleAuth::getAuthName).collect(Collectors.toList());
        if (sysAuthNames.contains("delete")){
            return sysProjectMapper.deleteByPrimaryKey(projectId) > 0;
        }else {
            throw new AuthenticationException("You do not have permission to delete this item");
        }
    }

    @SneakyThrows
    public Boolean archiveProject(String projectId, String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return false;
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        if (!authMap.containsKey(projectId)) {
            throw new AuthenticationException("You do not have permission to file this item");
        }

        SysProject sysProject = sysProjectMapper.selectByPrimaryKey(projectId);
        if (sysProject == null){
            throw new UnsupportedOperationException("Archiving failed! The item could not be found！");
        }
        String role = authMap.get(projectId);
        Example example = new Example(SysRoleAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleName", role);
        List<SysRoleAuth> sysRoleAuths = sysRoleAuthMapper.selectByExample(example);
        List<String> sysAuthNames = sysRoleAuths.stream().map(SysRoleAuth::getAuthName).collect(Collectors.toList());
        if (sysAuthNames.contains("archive")){
            sysProject.setStatus(1);
            return sysProjectMapper.updateByPrimaryKey(sysProject) > 0;
        }else {
            throw new AuthenticationException("You do not have permission to file this item");
        }
    }

    @SneakyThrows
    public Boolean inviteCollaborator(String projectId, String inviteeUserId, String useId) {
        String authRedisKey = DemoConstant.getAuthRedisKey(useId);
        String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
        if (StringUtils.isEmpty(auth)) {
            return false;
        }
        Map<String, String> authMap = JSON.parseObject(auth, HashMap.class);
        if (!authMap.containsKey(projectId)) {
            throw new AuthenticationException("You do not have permission to invite collaborators on this project");
        }
        SysProject sysProject = sysProjectMapper.selectByPrimaryKey(projectId);
        if (sysProject == null){
            throw new UnsupportedOperationException("Invitation failed! This item cannot be found!");
        }
        String role = authMap.get(projectId);
        Example example = new Example(SysRoleAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleName", role);
        List<SysRoleAuth> sysRoleAuths = sysRoleAuthMapper.selectByExample(example);
        List<String> sysAuthNames = sysRoleAuths.stream().map(SysRoleAuth::getAuthName).collect(Collectors.toList());
        if (sysAuthNames.contains("invite")){
            SysUserProject sysUserProject = new SysUserProject();
            sysUserProject.setId(UUID.randomUUID().toString());
            sysUserProject.setUserId(inviteeUserId);
            sysUserProject.setUserRole("teamworker");
            sysUserProject.setProjectId(projectId);
            sysUserProjectMapper.insert(sysUserProject);
        }else {
            throw new AuthenticationException("You do not have permission to invite collaborators on this project");
        }
        return true;
    }
}