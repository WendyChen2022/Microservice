package com.example.web.iam;

import com.example.common.entity.Result;
import com.example.common.model.SysProject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(value = "iam-server")
@RequestMapping("/project")
public interface ProjectFeignClient {

    @GetMapping("/{projectId}")
    Result<SysProject> findById(@PathVariable(name = "projectId") String projectId);

}
