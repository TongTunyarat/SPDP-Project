package com.example.project.repository;

import com.example.project.entity.Account;
import com.example.project.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, String> {
    Instructor findByAccount(Account account);
}

