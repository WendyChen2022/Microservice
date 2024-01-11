package com.example.iam.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.constat.DemoConstant;
import com.example.common.dto.LoginDto;
import com.example.common.dto.RegisterDto;
import com.example.common.model.SysUser;
import com.example.common.model.SysUserProject;
import com.example.common.util.BcryptUtil;
import com.example.common.entity.Result;
import com.example.iam.dao.SysUserMapper;
import com.example.iam.dao.SysUserProjectMapper;
import com.example.iam.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.common.util.JwtUtil;
import com.example.common.entity.StatusCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import tk.mybatis.mapper.entity.Example;


@RestController
@RequestMapping("/user")
@CrossOrigin
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserProjectMapper userProjectMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * User registration
     *
     * @param registerDto Registration information
     * @return Registered user object
     */
    @PostMapping("/register")
    public Result<RegisterDto> register(@RequestBody @Valid RegisterDto registerDto) {
        Assert.assertNotNull("Registration information cannot be null", registerDto);
        Example example = sysUserService.createExample(registerDto);
        //1. Query the user object corresponding to the username from the database
        List<SysUser> sysUserList = sysUserMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(sysUserList)) {
            throw new IllegalArgumentException("account/phone/username has been registered");
        }
        //3. Encrypt password
        String secretKey = BcryptUtil.doEncrypt(registerDto.getPassword());
        SysUser sysUser = new SysUser();
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setAccount(registerDto.getAccount());
        sysUser.setUsername(registerDto.getUsername());
        sysUser.setPhone(registerDto.getPhone());
        sysUser.setPassword(secretKey);
        sysUserService.add(sysUser);
        return new Result<RegisterDto>(true, StatusCode.OK, "Registration successful", registerDto);
    }

    /**
     * User login
     *
     * @param loginDto Login information
     * @param response HttpServletResponse
     * @param request  HttpServletRequest
     * @return Logged in user object
     */
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody LoginDto loginDto, HttpServletResponse response, HttpServletRequest request) {
        Assert.assertNotNull("Account cannot be null", loginDto.getAccount());
        Assert.assertNotNull("Password cannot be null", loginDto.getPassword());
        //1. Query the user object corresponding to the username from the database
        SysUser sysUser = sysUserService.findByAccount(loginDto.getAccount());
        if (sysUser == null) {
            //2. If the user is null, return an error message
            return new Result<SysUser>(false, StatusCode.LOGINERROR, "Incorrect account or password");
        }
        //3. If the user is not null, check if the password is correct
        String password = loginDto.getPassword();
        String encodePassword = BcryptUtil.doEncrypt(password);
        if (BcryptUtil.matchPassword(loginDto.getPassword(), encodePassword)) {

            String jwtRedisKey = DemoConstant.getJwtRedisKey(sysUser.getId());
            String jwt = stringRedisTemplate.opsForValue().get(jwtRedisKey);
            if (StringUtils.isEmpty(jwt)) {
                // Success
                Map<String, Object> userInfo = new HashMap<String, Object>();
                userInfo.put("id", sysUser.getId());
                //1. Generate a token
                jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(userInfo), null);
                // Set the cache time to 2 hours
                stringRedisTemplate.opsForValue().set(jwtRedisKey, jwt, 2, TimeUnit.HOURS);
            }

            String authRedisKey = DemoConstant.getAuthRedisKey(sysUser.getId());
            String auth = stringRedisTemplate.opsForValue().get(authRedisKey);
            if (StringUtils.isEmpty(auth)) {
                Example example = new Example(SysUserProject.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("userId", sysUser.getId());
                List<SysUserProject> sysUserProjects = userProjectMapper.selectByExample(example);
                Map<String, String> roleMap = new HashMap<String, String>();
                //search and create user's project-role Map
                for (SysUserProject sysUserProject : sysUserProjects) {
                    roleMap.put(sysUserProject.getProjectId(), sysUserProject.getUserRole());
                }
                String roleMapStr = JSON.toJSONString(roleMap);
                stringRedisTemplate.opsForValue().set(authRedisKey, roleMapStr, 2, TimeUnit.HOURS);
            }

            //2. Set the token in the cookie
            Cookie cookie = new Cookie("Authorization", jwt);
            response.addCookie(cookie);
            //3. Set the token in the header
            response.setHeader("Authorization", jwt);
            return new Result<SysUser>(true, StatusCode.OK, "Login successful", jwt);
        } else {
            // Failed
            return new Result<SysUser>(false, StatusCode.LOGINERROR, "Incorrect account or password");
        }
    }

    /**
     * Query user information by user ID
     *
     * @param userId User ID
     * @return User object
     */
    @GetMapping("/{userId}")
    public Result<SysUser> findById(@PathVariable String userId) {
        Assert.assertNotNull("User ID cannot be null", userId);
        // Call the UserService to query User by primary key
        SysUser sysUser = sysUserService.findById(userId);
        return new Result<SysUser>(true, StatusCode.OK, "Query successful", sysUser);
    }

    /**
     * Query all users
     *
     * @return List of user objects
     */
    @GetMapping("/all")
    public Result<List<SysUser>> findAll() {
        // Call the UserService to query all Users
        List<SysUser> list = sysUserService.findAll();
        return new Result<List<SysUser>>(true, StatusCode.OK, "Query successful", list);
    }
}