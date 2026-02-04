package com.tasksphere.repository;

import com.tasksphere.model.Team;
import com.tasksphere.model.TeamMember;
import com.tasksphere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember , Long> {

    List<TeamMember> findByTeam(Team team);

    Optional<TeamMember> findByTeamAndUser(Team team , User user);

    boolean existsByTeamAndUser(Team team , User user);
}
