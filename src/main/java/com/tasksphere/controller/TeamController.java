package com.tasksphere.controller;


import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
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
}
