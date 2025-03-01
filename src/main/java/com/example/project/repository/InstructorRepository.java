package com.example.project.repository;

import com.example.project.entity.Account;
import com.example.project.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, String> {

    Instructor findByAccount(Account account);

    // Query to find Instructor by their ID
    Instructor findByProfessorId(String instructorId);

}

