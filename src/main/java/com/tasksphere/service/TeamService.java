package com.tasksphere.service;

import com.tasksphere.dto.TeamRequestDto;
import com.tasksphere.dto.TeamResponseDto;
import com.tasksphere.enums.TeamRole;
import com.tasksphere.exception.BadRequestException;
import com.tasksphere.exception.ForbiddenException;
import com.tasksphere.exception.ResourceNotFoundException;
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
        member.setRole(TeamRole.OWNER);

        teamMemberRepository.save(member);

        // Return DTO
        return new TeamResponseDto(
                saved.getId(),
                saved.getName()
        );
    }

    //Delete team

    public void deleteTeam(Long teamId) {

        User user = getCurrentUser();
        Team team = getTeam(teamId);

        TeamMember member = getMemberOrThrow(team, user);

        requireOwner(member);

        teamRepository.delete(team);
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
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found : " + teamId ));

        return teamMemberRepository.findByTeam(team)
                .stream()
                .map(m-> m.getUser().getEmail() + " (" + m.getRole() + ")")
                .toList();
    }

    public void inviteMember(Long teamId, String email){

        //replaced earlier logic with functional logic to enhance reusability
        Team team = getTeam(teamId);

        User current = getCurrentUser();

        TeamMember member = getMemberOrThrow(team , current);

        requireAdminOrOwner(member);

        if(teamInvitationRepository.findByTeamAndEmail(team,email).isPresent()){
            throw new BadRequestException("Already Invited");
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

    private void requireOwner(TeamMember member) {
        if(member.getRole() != TeamRole.OWNER) {
            throw new ForbiddenException("Only owner allowed");
        }
    }

    private void requireAdminOrOwner(TeamMember member) {
        if(member.getRole() == TeamRole.MEMBER) {
            throw new ForbiddenException("Only admin or owner allowed");
        }
    }

    private TeamMember getMemberOrThrow(Team team , User user) {
        return teamMemberRepository.findByTeamAndUser(team,user)
                .orElseThrow(() -> new RuntimeException("Not a team member"));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public void removeMember(Long teamid , Long userId) {

        User current = getCurrentUser();

        Team team = getTeam(teamid);

        TeamMember requester = getMemberOrThrow(team , current);

        //Admin or owner only
        requireAdminOrOwner(requester);

        User targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        TeamMember targetMember = teamMemberRepository.findByTeamAndUser(team,targetUser)
                .orElseThrow(() -> new RuntimeException("Not a team member"));

        //Owner cannot be removed
        if(targetMember.getRole() == TeamRole.OWNER){
            throw new ForbiddenException("Cannot remove owner");
        }

        teamMemberRepository.delete(targetMember);
    }

    public void promoteMember(Long teamId , Long userId) {

        User current = getCurrentUser();

        Team team = getTeam(teamId);

        TeamMember requester = getMemberOrThrow(team , current);

        //Only owner
        requireOwner(requester);

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamMember targetMember = teamMemberRepository.findByTeamAndUser(team , targetUser)
                .orElseThrow(() -> new RuntimeException("Not a team member"));

        //Cannot promote owner
        if(targetMember.getRole() == TeamRole.OWNER) {
            throw new ForbiddenException("Cannot promote owner");
        }

        targetMember.setRole(TeamRole.ADMIN);

        teamMemberRepository.save(targetMember);
    }

    public void demoteMember(Long teamId, Long userId){

        User current = getCurrentUser();

        Team team = getTeam(teamId);

        TeamMember requester = getMemberOrThrow(team, current);

        // Only OWNER
        requireOwner(requester);

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamMember targetMember =
                teamMemberRepository
                        .findByTeamAndUser(team, targetUser)
                        .orElseThrow(() -> new RuntimeException("Not a team member"));

        // Cannot demote OWNER
        if(targetMember.getRole() == TeamRole.OWNER){
            throw new ForbiddenException("Cannot demote owner");
        }

        targetMember.setRole(TeamRole.MEMBER);

        teamMemberRepository.save(targetMember);
    }

    public void transferOwnership(Long teamId, Long newOwnerId){

        User current = getCurrentUser();

        Team team = getTeam(teamId);

        TeamMember currentOwner = getMemberOrThrow(team, current);

        // Must be owner
        requireOwner(currentOwner);

        User newOwnerUser = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamMember newOwner =
                teamMemberRepository
                        .findByTeamAndUser(team, newOwnerUser)
                        .orElseThrow(() -> new RuntimeException("User not in team"));

        // Downgrade old owner
        currentOwner.setRole(TeamRole.ADMIN);

        // Upgrade new owner
        newOwner.setRole(TeamRole.OWNER);

        // Update team owner
        team.setOwner(newOwnerUser);

        teamMemberRepository.save(currentOwner);
        teamMemberRepository.save(newOwner);
        teamRepository.save(team);
    }



}
