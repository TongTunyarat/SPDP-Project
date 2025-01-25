package com.example.project.repository;

import com.example.project.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

// <<<<<<< Nref
// public interface AccountRepository extends JpaRepository<Account, Integer> {
// }
// =======
public interface AccountRepository extends JpaRepository<Account, String> {
}
// >>>>>>> Tong
