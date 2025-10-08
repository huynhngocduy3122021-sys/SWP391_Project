package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    // Custom method to find an Admin by email (useful for login/checking uniqueness)
    Optional<Admin> findByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);


}
