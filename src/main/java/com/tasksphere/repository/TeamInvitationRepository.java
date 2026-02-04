package com.tasksphere.repository;

import com.tasksphere.model.Team;
import com.tasksphere.model.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {

    List<TeamInvitation> findByEmail(String email);

    Optional<TeamInvitation> findByTeamAndEmail(Team team , String email);

    void deleteByTeamAndEmail(Team team , String email);
}
