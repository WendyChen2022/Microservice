package com.example.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class LoginDto implements Serializable {

    @ApiModelProperty(value = "account", required = true)
    private String account;

    @ApiModelProperty(value = "password", required = true)
    private String password;

}
