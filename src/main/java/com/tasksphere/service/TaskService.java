package com.tasksphere.service;

import com.tasksphere.enums.TaskStatus;
import com.tasksphere.enums.TeamRole;
import com.tasksphere.exception.TaskNotFoundException;
import com.tasksphere.model.Task;
import com.tasksphere.model.Team;
import com.tasksphere.model.TeamMember;
import com.tasksphere.model.User;
import com.tasksphere.repository.TaskRepository;
import com.tasksphere.dto.TaskRequestDto;
import com.tasksphere.dto.TaskResponseDto;


import com.tasksphere.repository.TeamMemberRepository;
import com.tasksphere.repository.TeamRepository;
import com.tasksphere.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;


@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TaskService(TaskRepository taskRepository , UserRepository userRepository , TeamRepository teamRepository , TeamMemberRepository teamMemberRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public TaskResponseDto createTask(TaskRequestDto dto){

        //edit for collaboration
        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(dto.getTitle()); // these all are for handling the client request
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setUser(user);

        Task saved = taskRepository.save(task); // this is for saving to to repository

        //return new TaskResponseDto(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus()); // response that doesn't at all concern with the db
        return TaskResponseDto.fromEntity(saved);
    }

    public Page<TaskResponseDto> getAllTasks(TaskStatus status , Pageable pageable){

        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Task> tasks;

        if(status != null){
            tasks = taskRepository.findByUserAndStatus(user ,status , pageable);
        } else {
            tasks = taskRepository.findByUser(user, pageable);
        }


        return tasks.map(TaskResponseDto::fromEntity); //earlier we used to form a new response dto , now we are using the factory method
    }


    public Task getTaskById(Long id){

        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if(!task.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not allowed to access this task");
        }

        return task;
    }


    public void deleteTaskById(Long id) {
        Task task = getTaskById(id); //checks ownership
        taskRepository.delete(task);
    }

    public TaskResponseDto updateTask(Long id , TaskRequestDto updatedTaskDto){
        Task existingTask = getTaskById(id); // ownership check

        existingTask.setTitle(updatedTaskDto.getTitle());
        existingTask.setDescription(updatedTaskDto.getDescription());
        existingTask.setStatus(updatedTaskDto.getStatus());

        Task saved = taskRepository.save(existingTask);

        //return new TaskResponseDto(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
        //same as create task

        return TaskResponseDto.fromEntity(saved);
    }

    private String getCurrentUserEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Page<TaskResponseDto> getAllTasksForAdmin(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(TaskResponseDto::fromEntity);
    }

    public TaskResponseDto createTeamTask(Long teamId, TaskRequestDto dto) {
        User user = getCurrentUser();

        Team team = getTeam(teamId);

        getMemberOrThrow(team,user); //checks membership

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setUser(user);
        task.setTeam(team);

        Task saved = taskRepository.save(task);

        return TaskResponseDto.fromEntity(saved);
    }

    public Page<TaskResponseDto> getTeamTasks(Long teamId, Pageable pageable){

        User user = getCurrentUser();

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found : " + teamId));

        teamMemberRepository
                .findByTeamAndUser(team , user)
                .orElseThrow(() -> new RuntimeException("Not team member"));

        return taskRepository.findByTeam(team , pageable)
                .map(TaskResponseDto::fromEntity);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        System.out.println("AUTH EMAIL =" + email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found : " + email));
    }

    private void requireOwner(TeamMember member) {
        if(member.getRole() != TeamRole.OWNER) {
            throw new RuntimeException("Only owner allowed");
        }
    }

    private void requireAdminOrOwner(TeamMember member) {
        if(member.getRole() == TeamRole.MEMBER) {
            throw new RuntimeException("Only admin or owner allowed");
        }
    }

    private TeamMember getMemberOrThrow(Team team , User user) {
        return teamMemberRepository.findByTeamAndUser(team,user)
                .orElseThrow(() -> new RuntimeException("Not a team member"));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

}
