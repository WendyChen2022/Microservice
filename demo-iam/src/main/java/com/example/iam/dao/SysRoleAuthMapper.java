package com.example.iam.dao;

import com.example.common.model.SysRoleAuth;
import com.example.common.model.SysUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface SysRoleAuthMapper extends Mapper<SysRoleAuth> {
}
