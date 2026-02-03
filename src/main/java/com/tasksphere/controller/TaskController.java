package com.tasksphere.controller;

import com.tasksphere.dto.TaskRequestDto;
import com.tasksphere.dto.TaskResponseDto;
import com.tasksphere.enums.TaskStatus;
import com.tasksphere.model.Task;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tasksphere.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

@RestController
@RequestMapping("/tasks")
public class TaskController {

     private final TaskService taskService ;

     public TaskController(TaskService taskService){
         this.taskService = taskService;
     }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody TaskRequestDto dto){
        return taskService.createTask(dto);
    }

    @GetMapping
    public Page<TaskResponseDto> getAllTasks(@RequestParam(required = false) TaskStatus status ,
                                  Pageable pageable){             // param may or may not exist , if not there -> null
         return taskService.getAllTasks(status , pageable);
    }
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable long id){
         return taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable long id){
         taskService.deleteTaskById(id);
    }
    // This is a constructor based Dependency injection

    @PutMapping("/{id}")
    public TaskResponseDto updateTaskById(@PathVariable Long id ,@Valid @RequestBody TaskRequestDto dto){
         return taskService.updateTask(id , dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all-tasks")
    public Page<TaskResponseDto> getAllTasksForAdmin(Pageable pageable) {
         return taskService.getAllTasksForAdmin(pageable);
    }



}
