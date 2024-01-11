package com.example.iam;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.iam.dao")
 public class IamServerApplication
{
    public static void main(String[] args) {
        SpringApplication.run(IamServerApplication.class,args);
        System.out.println("Hello IAM!");
    }
}
