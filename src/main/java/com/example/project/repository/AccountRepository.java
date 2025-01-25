package com.example.project.repository;

import com.example.project.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// <<<<<<< Nref
// public interface AccountRepository extends JpaRepository<Account, Integer> {
// }
// =======
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional findAccountByUsername(String username);
}
// >>>>>>> Tong
