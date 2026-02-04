package com.tasksphere.service;

import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
import com.tasksphere.model.Team;
import com.tasksphere.model.User;
import com.tasksphere.repository.TeamRepository;
import com.tasksphere.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository , UserRepository userRepository){
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public TeamResponseDto createTeam(TeamRequestDto dto){

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = new Team();
        team.setName(dto.getName());
        team.setOwner(owner);

        Team saved = teamRepository.save(team);

        return new TeamResponseDto(saved.getId() , saved.getName());
    }

    public List<TeamResponseDto> getMyTeams() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        return teamRepository.findByOwner(user)
                .stream()
                .map(t -> new TeamResponseDto(t.getId() , t.getName()))
                .toList();
    }

    //The code which finds the email - It checks who is currently logged in .
    //check obsidian notes for more info
}
