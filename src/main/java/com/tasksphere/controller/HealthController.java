package com.tasksphere.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  //marks the class as API handler
public class HealthController {

    @GetMapping("/health")  //maps to get request
    public String healthCheck() {
        return "TaskSphere backend in running";
    }
}
