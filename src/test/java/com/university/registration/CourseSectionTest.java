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

class CourseSectionTest {

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
    void seatsRemainingWorks() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        CourseSection section = new CourseSection("A", 2, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        student.enroll(section, semester);
        assertEquals(1, section.seatsRemaining());
    }

    @Test
    void waitlistPromotionWorks() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        CourseSection section = new CourseSection("A", 1, course, semester);
        Student s1 = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);
        Student s2 = new Student("S2", "Bob", "b@u.com", "bob", "pass", 9, degreeProgram);

        s1.enroll(section, semester);
        s2.enroll(section, semester);

        s1.drop(section, semester, LocalDate.now());

        assertEquals(EnrollmentStatus.DROPPED, s1.getEnrollments().get(0).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, s2.getEnrollments().get(0).getStatus());
        assertEquals(1, section.getEnrolledCount());
    }
}

