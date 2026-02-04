package com.tasksphere.controller;


import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
import com.tasksphere.model.TeamInvitation;
import com.tasksphere.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService){
        this.teamService = teamService;
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
}
