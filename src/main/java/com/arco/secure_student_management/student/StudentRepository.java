package com.arco.secure_student_management.student;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<Student, Long> {

    Optional<Student> findByRegistrationNumber(
            String registrationNumber
    );

    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(
            String registrationNumber
    );
}
