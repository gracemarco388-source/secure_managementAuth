package com.arco.secure_student_management.student;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arco.secure_student_management.exception.ResourceNotFoundException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(
            StudentRepository studentRepository
    ) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {

        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {

        return studentRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );
    }

    public Student createStudent(Student student) {

        if (studentRepository.existsByEmail(
                student.getEmail()
        )) {

            throw new IllegalArgumentException(
                    "Student email already exists"
            );
        }

        if (studentRepository.existsByRegistrationNumber(
                student.getRegistrationNumber()
        )) {

            throw new IllegalArgumentException(
                    "Registration number already exists"
            );
        }

        return studentRepository.save(student);
    }

    public Student updateStudent(
            Long id,
            Student updatedStudent
    ) {

        Student existingStudent =
                getStudentById(id);

        if (!existingStudent.getEmail()
                .equals(updatedStudent.getEmail())
                && studentRepository.existsByEmail(
                updatedStudent.getEmail()
        )) {

            throw new IllegalArgumentException(
                    "Student email already exists"
            );
        }

        if (!existingStudent
                .getRegistrationNumber()
                .equals(
                        updatedStudent.getRegistrationNumber()
                )
                && studentRepository
                .existsByRegistrationNumber(
                        updatedStudent.getRegistrationNumber()
                )) {

            throw new IllegalArgumentException(
                    "Registration number already exists"
            );
        }

        existingStudent.setFirstName(
                updatedStudent.getFirstName()
        );

        existingStudent.setLastName(
                updatedStudent.getLastName()
        );

        existingStudent.setEmail(
                updatedStudent.getEmail()
        );

        existingStudent.setRegistrationNumber(
                updatedStudent.getRegistrationNumber()
        );

        existingStudent.setCourse(
                updatedStudent.getCourse()
        );

        existingStudent.setAge(
                updatedStudent.getAge()
        );

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {

        Student student =
                getStudentById(id);

        studentRepository.delete(student);
    }
}
