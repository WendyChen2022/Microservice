package com.example.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class RegisterDto  implements Serializable {

    @ApiModelProperty(value = "account", required = true)
    @NotBlank(message = "The account cannot be empty")
    private String account;

    @ApiModelProperty(value = "password", required = true)
    @NotBlank(message = "The password cannot be empty")
    private String password;

    @ApiModelProperty(value = "phone", required = true)
    @NotBlank(message = "The phone cannot be empty")
    private String phone;

    @ApiModelProperty(value = "username", required = true)
    @NotBlank(message = "The username cannot be empty")
    private String username;

}
