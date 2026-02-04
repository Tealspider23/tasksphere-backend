package com.tasksphere.repository;

import com.tasksphere.model.Team;
import com.tasksphere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team , Long> {
    List<Team> findByOwner(User owner);
}
