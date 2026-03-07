package com.university.registration;

import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.Enrollment;
import com.university.registration.model.EnrollmentStatus;
import com.university.registration.model.Instructor;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstructorTest {

    @Test
    void instructorCanViewAssignedSections() {
        Instructor instructor = new Instructor("I1", "Prof", "prof@u.com", "prof", "pass");
        Instructor other = new Instructor("I2", "Other", "other@u.com", "other", "pass");
        Department department = new Department("COMP", "Computer Science");
        Course course = new Course("COMP248", "OOP I", 3, department);
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        CourseSection s1 = new CourseSection("A", 30, course, semester);
        CourseSection s2 = new CourseSection("B", 30, course, semester);
        s1.setInstructor(instructor);
        s2.setInstructor(other);
        semester.getSections().add(s1);
        semester.getSections().add(s2);

        List<CourseSection> assigned = instructor.viewAssignedSections(semester);
        assertEquals(1, assigned.size());
        assertEquals("A", assigned.get(0).getSectionId());
    }

    @Test
    void instructorCanAssignGradeToStudentEnrollment() {
        Instructor instructor = new Instructor("I1", "Prof", "prof@u.com", "prof", "pass");
        DegreeProgram program = new DegreeProgram("BSc CS", 120);
        Department department = new Department("COMP", "Computer Science");
        Course course = new Course("COMP248", "OOP I", 3, department);
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        CourseSection section = new CourseSection("A", 30, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, program);

        Enrollment enrollment = new Enrollment(LocalDateTime.now(), EnrollmentStatus.ENROLLED, student, section);
        section.getEnrollments().add(enrollment);
        student.getEnrollments().add(enrollment);

        instructor.addStudentGrade(section, student, "A");
        assertEquals("A", enrollment.getGrade());
    }

    @Test
    void instructorCanViewOnlyOwnSectionEnrollments() {
        Instructor instructor = new Instructor("I1", "Prof", "prof@u.com", "prof", "pass");
        Instructor other = new Instructor("I2", "Other", "other@u.com", "other", "pass");
        DegreeProgram program = new DegreeProgram("BSc CS", 120);
        Department department = new Department("COMP", "Computer Science");
        Course course = new Course("COMP248", "OOP I", 3, department);
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        CourseSection ownSection = new CourseSection("A", 30, course, semester);
        ownSection.setInstructor(instructor);
        CourseSection foreignSection = new CourseSection("B", 30, course, semester);
        foreignSection.setInstructor(other);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, program);
        Enrollment enrollment = new Enrollment(LocalDateTime.now(), EnrollmentStatus.ENROLLED, student, ownSection);
        ownSection.getEnrollments().add(enrollment);

        assertEquals(1, instructor.viewEnrolledStudents(ownSection).size());
        assertThrows(IllegalStateException.class, () -> instructor.viewEnrolledStudents(foreignSection));
    }
}
