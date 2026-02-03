package com.tasksphere.dto;

import com.tasksphere.enums.TaskStatus;
import com.tasksphere.model.Task;

public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;

    public TaskResponseDto(Long id , String title , String description , TaskStatus status){
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }
    //Factory method
    public static TaskResponseDto fromEntity(Task task){
        return new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
