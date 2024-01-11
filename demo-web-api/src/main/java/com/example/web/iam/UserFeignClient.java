package com.example.web.iam;

import com.example.common.entity.Result;
import com.example.common.model.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(value = "iam-server")
@RequestMapping("/user")
public interface UserFeignClient {

    @GetMapping("/{userId}")
    Result<SysUser> findById(@PathVariable(name = "userId") String userId);

}
