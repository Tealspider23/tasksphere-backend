package com.tasksphere.controller;


import com.tasksphere.dto.TaskRequestDto;
import com.tasksphere.dto.TaskResponseDto;
import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
import com.tasksphere.model.TeamInvitation;
import com.tasksphere.service.TaskService;
import com.tasksphere.service.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final TaskService taskService;

    public TeamController(TeamService teamService , TaskService taskService){
        this.teamService = teamService;
        this.taskService = taskService;
    }

    @PostMapping
    public TeamResponseDto createTeam(@RequestBody TeamRequestDto dto){
        return teamService.createTeam(dto);
    }

    @GetMapping
    public List<TeamResponseDto> getMyTeams() {
        return teamService.getMyTeams();
    }

    @GetMapping("/{id}/members")
    public List<String> getMembers(@PathVariable Long id){
        return teamService.getMembers(id);
    }

    @GetMapping("/invites")
    public List<TeamInvitation> myInvites() {
        return teamService.getMyInvites();
    }

    @PostMapping("/{id}/invite")
    public void invite(@PathVariable Long id , @RequestParam String email){
        teamService.inviteMember(id , email);
    }

    @PostMapping("/invites/{inviteId}/accept")
    public void accept(@PathVariable Long inviteId){
        teamService.acceptInvite(inviteId);
    }

    @PostMapping("/{id}/tasks")
    public TaskResponseDto createTeamTask(@PathVariable Long id , @RequestBody TaskRequestDto dto){
        return taskService.createTeamTask(id,dto);
    }

    @GetMapping("/{id}/tasks")
    public Page<TaskResponseDto> getTeamTasks(@PathVariable Long id , Pageable pageable){
        return taskService.getTeamTasks(id, pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public void removeMember(@PathVariable Long teamId , @PathVariable Long userId) {
        teamService.removeMember(teamId , userId);
    }

    @PostMapping("/{teamId}/members/{userId}/promote")
    public void promoteMember(@PathVariable Long teamId, @PathVariable Long userId){

        teamService.promoteMember(teamId, userId);
    }


    @PostMapping("/{teamId}/members/{userId}/demote")
    public void demoteMember(@PathVariable Long teamId, @PathVariable Long userId){

        teamService.demoteMember(teamId, userId);
    }

    @PostMapping("/{teamId}/transfer/{userId}")
    public void transferOwnership(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.transferOwnership(teamId,userId);
    }


}
