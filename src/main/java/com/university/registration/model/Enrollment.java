package com.university.registration.model;

import java.time.LocalDateTime;

public class Enrollment {
    private LocalDateTime enrollmentDateTime;
    private EnrollmentStatus status;
    private String grade;
    private Student student;
    private CourseSection section;

    public Enrollment(LocalDateTime enrollmentDateTime, EnrollmentStatus status, Student student, CourseSection section) {
        this.enrollmentDateTime = enrollmentDateTime;
        this.status = status;
        this.student = student;
        this.section = section;
    }

    public LocalDateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(LocalDateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseSection getSection() {
        return section;
    }

    public void setSection(CourseSection section) {
        this.section = section;
    }
}

