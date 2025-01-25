package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}


}

//	@Bean
//	public CommandLineRunner commandLineRunner(){
//		return args-> {
//			System.out.println("Hello database");
//		};
//	}

//	public CommandLineRunner commandLineRunner(ProjectDAO dao) {
//		return runner-> {
//			getAllData(dao);
//		};
//	}
//
//	public void getAllData(ProjectDAO dao) {
//		List<Project> data = dao.getAll();
//		for(Project project : data) {
//			System.out.println(project);
//		}
//	}
