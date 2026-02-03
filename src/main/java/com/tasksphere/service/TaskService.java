package com.tasksphere.service;

import com.tasksphere.enums.TaskStatus;
import com.tasksphere.exception.TaskNotFoundException;
import com.tasksphere.model.Task;
import com.tasksphere.model.User;
import com.tasksphere.repository.TaskRepository;
import com.tasksphere.dto.TaskRequestDto;
import com.tasksphere.dto.TaskResponseDto;


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

    public TaskService(TaskRepository taskRepository , UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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

}
