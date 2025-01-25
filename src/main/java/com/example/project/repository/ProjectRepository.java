package com.example.project.repository;

import com.example.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

// <<<<<<< Nref
// public interface ProjectRepository extends JpaRepository<Project, Integer> {

// }
// =======
public interface ProjectRepository extends JpaRepository<Project, String> {

}
// >>>>>>> Tong
