package com.example.iam.service;

import com.example.common.dto.RegisterDto;
import com.example.common.model.SysUser;
import com.example.common.util.BcryptUtil;
import com.example.iam.dao.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.UUID;


@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * Create a query object for User
     *
     * @param user User object
     * @return Example object
     */
    public Example createExample(RegisterDto user) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        if (user != null) {
            // Account
            if (!StringUtils.isEmpty(user.getAccount())) {
                criteria.andEqualTo("account", user.getAccount());
            }
            // Username
            if (!StringUtils.isEmpty(user.getUsername())) {
                criteria.orEqualTo("username", user.getUsername());
            }
            // Phone number
            if (!StringUtils.isEmpty(user.getPhone())) {
                criteria.orEqualTo("phone", user.getPhone());
            }
        }
        return example;
    }

    public void register(RegisterDto registerDto) {

    }

    /**
     * Delete User
     *
     * @param userId User ID
     */
    public void delete(String userId) {
        sysUserMapper.deleteByPrimaryKey(userId);
    }

    /**
     * Update User
     *
     * @param sysUser User object
     */
    public void update(SysUser sysUser) {
        sysUserMapper.updateByPrimaryKey(sysUser);
    }

    /**
     * Add User
     *
     * @param sysUser User object
     */
    public void add(SysUser sysUser) {
        sysUserMapper.insert(sysUser);
    }

    /**
     * Query User by USERID
     *
     * @param userId User ID
     * @return User object
     */
    public SysUser findById(String userId) {
        return sysUserMapper.selectByPrimaryKey(userId);
    }

    /**
     * Query User by ACCOUNT
     *
     * @param account Account
     * @return User object
     */
    public SysUser findByAccount(String account) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        // Account
        criteria.andEqualTo("account", account);
        return sysUserMapper.selectOneByExample(example);
    }

    /**
     * Query all User data
     *
     * @return List of User objects
     */
    public List<SysUser> findAll() {
        return sysUserMapper.selectAll();
    }
}
