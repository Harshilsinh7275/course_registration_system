package com.university.registration.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Instructor extends Person {
    public Instructor(String id, String name, String email, String username, String password) {
        super(id, name, email, username, password);
    }

    public List<CourseSection> viewAssignedSections(Semester semester) {
        return semester.getSections().stream()
                .filter(section -> Objects.equals(section.getInstructor(), this))
                .collect(Collectors.toList());
    }

    public void addStudentGrade(CourseSection section, Student student, String grade) {
        section.getEnrollments().stream()
                .filter(enrollment -> enrollment.getStudent().equals(student))
                .findFirst()
                .ifPresent(enrollment -> enrollment.setGrade(grade));
    }

    public List<Enrollment> viewEnrolledStudents(CourseSection section) {
        if (!Objects.equals(section.getInstructor(), this)) {
            throw new IllegalStateException("Instructor can only view students for assigned sections.");
        }
        return section.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .collect(Collectors.toList());
    }
}
