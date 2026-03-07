package com.university.registration;

import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.EnrollmentStatus;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {

    private Department department;
    private DegreeProgram degreeProgram;
    private Semester semester;

    @BeforeEach
    void setUp() {
        department = new Department("COMP", "Computer Science");
        degreeProgram = new DegreeProgram("BSc CS", 120);
        semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5)
        );
    }

    @Test
    void successfulEnrollment() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        CourseSection section = new CourseSection("A", 2, course, semester);
        semester.getSections().add(section);

        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);
        student.enroll(section, semester);

        assertEquals(1, student.getEnrollments().size());
        assertEquals(EnrollmentStatus.ENROLLED, student.getEnrollments().get(0).getStatus());
    }

    @Test
    void waitlistWhenSectionFull() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        CourseSection section = new CourseSection("A", 1, course, semester);
        semester.getSections().add(section);

        Student s1 = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);
        Student s2 = new Student("S2", "Bob", "b@u.com", "bob", "pass", 9, degreeProgram);

        s1.enroll(section, semester);
        s2.enroll(section, semester);

        assertEquals(EnrollmentStatus.ENROLLED, s1.getEnrollments().get(0).getStatus());
        assertEquals(EnrollmentStatus.WAITLISTED, s2.getEnrollments().get(0).getStatus());
    }

    @Test
    void cannotExceedCreditLimit() {
        Course course1 = new Course("COMP248", "OOP I", 3, department);
        Course course2 = new Course("COMP232", "Math", 1, department);
        CourseSection section1 = new CourseSection("A", 2, course1, semester);
        CourseSection section2 = new CourseSection("B", 2, course2, semester);
        semester.getSections().add(section1);
        semester.getSections().add(section2);

        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 3, degreeProgram);
        student.enroll(section1, semester);

        assertThrows(IllegalStateException.class, () -> student.enroll(section2, semester));
    }

    @Test
    void cannotEnrollWithoutPrerequisites() {
        Course prerequisite = new Course("COMP248", "OOP I", 3, department);
        Course target = new Course("COMP249", "OOP II", 3, department);
        target.addPrerequisite(prerequisite);

        CourseSection targetSection = new CourseSection("A", 2, target, semester);
        semester.getSections().add(targetSection);

        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        assertThrows(IllegalStateException.class, () -> student.enroll(targetSection, semester));
    }
}

