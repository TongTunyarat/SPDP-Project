package com.example.project.repository;

import com.example.project.entity.Account;
import com.example.project.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, String> {

    Instructor findByAccount(Account account);

    @Query("SELECT i FROM Instructor i JOIN i.account a WHERE a.username = :username")
    Optional<Instructor> findByAccountUsername(@Param("username") String username);

}

