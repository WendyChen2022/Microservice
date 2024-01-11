package com.example.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ProjectDto {

    @ApiModelProperty(value = "title", required = true)
    @NotBlank(message = "The title cannot be empty")
    private String title;

    @ApiModelProperty(value = "content", required = true)
    @NotBlank(message = "The content cannot be empty")
    private String content;

    @ApiModelProperty(value = "status", required = true)
    private Integer status;

    @ApiModelProperty(value = "picUrl", required = true)
    List<String> picUrl;

}
