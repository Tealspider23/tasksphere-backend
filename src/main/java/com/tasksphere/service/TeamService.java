package com.tasksphere.service;

import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
import com.tasksphere.model.Team;
import com.tasksphere.model.TeamInvitation;
import com.tasksphere.model.TeamMember;
import com.tasksphere.model.User;
import com.tasksphere.repository.TeamInvitationRepository;
import com.tasksphere.repository.TeamMemberRepository;
import com.tasksphere.repository.TeamRepository;
import com.tasksphere.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInvitationRepository teamInvitationRepository;

    public TeamService(TeamRepository teamRepository , UserRepository userRepository , TeamMemberRepository teamMemberRepository , TeamInvitationRepository teamInvitationRepository){
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInvitationRepository = teamInvitationRepository;
    }

    //helper
    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow();
    }
    //

    //Functions(main) -

    public TeamResponseDto createTeam(TeamRequestDto dto){

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create Team
        Team team = new Team();
        team.setName(dto.getName());
        team.setOwner(owner);

        Team saved = teamRepository.save(team);

        // Create TeamMember (OWNER)
        TeamMember member = new TeamMember();
        member.setTeam(saved);
        member.setUser(owner);
        member.setRole("OWNER");

        teamMemberRepository.save(member);

        // Return DTO
        return new TeamResponseDto(
                saved.getId(),
                saved.getName()
        );
    }


    public List<TeamResponseDto> getMyTeams() {

        //Getting the user logged in
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));

        return teamRepository.findByOwner(user)
                .stream()
                .map(t -> new TeamResponseDto(t.getId() , t.getName()))
                .toList();
    }

    public List<String> getMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Team not found : " + teamId ));

        return teamMemberRepository.findByTeam(team)
                .stream()
                .map(m-> m.getUser().getEmail() + " (" + m.getRole() + ")")
                .toList();
    }

    public void inviteMember(Long teamId, String email){

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));

        User inviter = getCurrentUser();

        TeamMember member = teamMemberRepository
                .findByTeamAndUser(team , inviter)
                .orElseThrow(() -> new RuntimeException("Not a team member"));

        if(!member.getRole().equals("OWNER") && !member.getRole().equals("ADMIN")){
            throw new RuntimeException("No permission");
        }

        if(teamInvitationRepository.findByTeamAndEmail(team,email).isPresent()){
            throw new RuntimeException("Already Invited");
        }

        TeamInvitation invite = new TeamInvitation();
        invite.setTeam(team);
        invite.setEmail(email);

        teamInvitationRepository.save(invite);
    }

    public void acceptInvite(Long inviteId) {

        TeamInvitation invite = teamInvitationRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));

        User user = getCurrentUser();

        if(!invite.getEmail().equals(user.getEmail())){
            throw new RuntimeException("Invalid invite");
        }

        if(teamMemberRepository.existsByTeamAndUser(invite.getTeam(),user)){
            throw new RuntimeException("Already member");
        }

        TeamMember member = new TeamMember();
        member.setTeam(invite.getTeam());
        member.setUser(user);
        member.setRole(invite.getRole());

        teamMemberRepository.save(member);

        teamInvitationRepository.delete(invite);

    }

    public List<TeamInvitation> getMyInvites() {
        User user = getCurrentUser();

        return teamInvitationRepository.findByEmail(user.getEmail());
    }

}
