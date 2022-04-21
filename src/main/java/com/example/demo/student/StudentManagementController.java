package com.example.demo.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

    private static final List<Student> studentList = List.of(
            new Student(1, "James Bond"),
            new Student(2, "Shay Gali"),
            new Student(3, "Anna Smith")
    );

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMIN_TRAINEE')")
    public List<Student> getStudentList() {
        return studentList;
    }

    @PostMapping
    public void registerNewStudent(@RequestBody Student student) {
        System.out.println(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable("id") Integer studentId) {
        System.out.println(studentId);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("id") Integer studentId, @RequestBody Student student) {
        System.out.printf("%s %s%n", studentId, student);
    }
}
