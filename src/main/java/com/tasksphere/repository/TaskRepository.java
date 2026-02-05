package com.tasksphere.repository;

import com.tasksphere.enums.TaskStatus;
import com.tasksphere.model.Task;
import com.tasksphere.model.Team;
import com.tasksphere.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task,Long> {

    Page<Task> findByUser(User user , Pageable pageable);

    Page<Task> findByUserAndStatus(User user , TaskStatus status , Pageable pageable); // Spring will automatically understand this

    Page<Task> findByUserAndTeamIsNull(User user , Pageable pageable);

    Page<Task> findByTeam(Team team , Pageable pageable);

    //Page<Task> findByIdAndTeam(Long id , Team team);

}
