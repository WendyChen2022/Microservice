package com.example.iam.controller;

import com.example.common.dto.ProjectDto;
import com.example.common.entity.Result;
import com.example.common.entity.StatusCode;
import com.example.common.model.SysProject;
import com.example.iam.service.SysProjectService;
import com.github.pagehelper.PageInfo;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.example.common.util.JwtUtil.getCurrentUserId;

@RestController
@RequestMapping("/project")
@CrossOrigin
public class SysProjectController {

    @Autowired
    private SysProjectService sysProjectService;

    /**
     * Create a project
     *
     * @param projectDto Project object
     * @return Created project object
     */
    @PostMapping("/create")
    public Result<SysProject> createProject(@RequestBody @Validated ProjectDto projectDto,
                                            @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        Assert.assertNotNull("Project cannot be null", projectDto);
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        SysProject sysProject = sysProjectService.createProject(projectDto, currentUserId);
        if (Objects.isNull(sysProject)) {
            return new Result<>(false, StatusCode.ERROR, "Failed to create project");
        } else {
            return new Result<>(true, StatusCode.OK, "Project created successfully", sysProject);
        }
    }

    /**
     * View details of a specific project
     *
     * @param projectId Project ID
     * @return Created project object
     */
    @PostMapping("/read/{projectId}")
    public Result<SysProject> readProject(@PathVariable String projectId, @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        Assert.assertNotNull("Project ID cannot be null", projectId);
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        SysProject sysProject = sysProjectService.readProject(projectId, currentUserId);
        if (sysProject != null) {
            return new Result<>(true, StatusCode.OK, "View project successfully", sysProject);
        } else {
            return new Result<>(false, StatusCode.ERROR, "The project does not exist");
        }
    }


    /**
     * Query page projects managed by the current user
     *
     * @param page: Current page
     * @param size: Number of items per page
     * @return PageInfo
     */
    @GetMapping(value = "/search")
    public Result<PageInfo> findPage(@RequestParam Integer page, @RequestParam Integer size,
                                     @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        PageInfo<SysProject> pageInfo = sysProjectService.findPage(page, size, currentUserId);
        return new Result<PageInfo>(true, StatusCode.OK, "Query successful", pageInfo);
    }

    /**
     * Query all projects managed by the current user
     *
     * @return Result<List<SysProject>>
     */
    @GetMapping("/all")
    public Result<List<SysProject>> findAll(@RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        List<SysProject> list = sysProjectService.findAll(currentUserId);
        return new Result<List<SysProject>>(true, StatusCode.OK, "Query successful", list);
    }

    /**
     * Update project information
     *
     * @param sysProject Updated project object
     * @return Updated project object
     */
    @PutMapping("/edit")
    public Result<SysProject> editProject(@RequestBody SysProject sysProject, @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        Assert.assertNotNull("Project ID cannot be null", sysProject.getId());
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        Boolean flag = sysProjectService.updateProject(sysProject, currentUserId);
        if (flag) {
            return new Result<>(true, StatusCode.OK, "Project updated successfully", sysProject);
        } else {
            return new Result<>(false, StatusCode.ERROR, "Failed to update project");
        }
    }

    /**
     * Delete a project
     *
     * @param projectId Project ID
     * @return Empty response
     */
    @DeleteMapping("/delete/{projectId}")
    public Result<String> delete(@PathVariable String projectId, @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        Assert.assertNotNull("Project ID cannot be null", projectId);
        // Get the value of Authorization from the request header
        String currentUserId = getCurrentUserId(request);
        Boolean flag = sysProjectService.deleteProject(projectId, currentUserId);
        if (flag) {
            return new Result<>(true, StatusCode.OK, "Project deleted successfully");
        } else {
            return new Result<>(false, StatusCode.ERROR, "Failed to delete project");
        }
    }

    /**
     * Archive a project
     *
     * @param projectId Project ID
     * @return Empty response
     */
    @DeleteMapping("/archive/{projectId}")
    public Result<String> archiveProject(@PathVariable String projectId, @RequestHeader("Authorization") String jwt, HttpServletRequest request) {
        Assert.assertNotNull("Project ID cannot be null", projectId);
        // Get the value of Authorization from the request header
        String auth = request.getHeader("Authorization");
        Boolean flag = sysProjectService.archiveProject(projectId, auth);
        if (flag) {
            return new Result<>(true, StatusCode.OK, "Project archived successfully");
        } else {
            return new Result<>(false, StatusCode.ERROR, "Failed to archive project");
        }
    }

    /**
     * Project owner invites collaborators to join the project
     *
     * @param projectId     Project ID
     * @param inviteeUserId Invitee's user ID
     * @return Empty response
     */
    @PostMapping("/invite/{projectId}")
    public Result<String> inviteCollaborator(@PathVariable String projectId,
                                             @RequestParam String inviteeUserId,
                                             @RequestHeader("Authorization") String jwt,
                                             HttpServletRequest request) throws Exception {
        // Get the value of Authorization from the request header
        String auth = request.getHeader("Authorization");
        // Set the default access control permission for the project to "view only"
        Boolean flag = sysProjectService.inviteCollaborator(projectId, inviteeUserId, auth);
        if (flag) {
            return new Result<>(true, StatusCode.OK, "Collaborator invited to join the project successfully");
        } else {
            return new Result<>(false, StatusCode.ERROR, "Failed to invite collaborator to join the project");
        }
    }
}