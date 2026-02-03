package com.tasksphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TasksphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasksphereApplication.class, args);
	}

}
