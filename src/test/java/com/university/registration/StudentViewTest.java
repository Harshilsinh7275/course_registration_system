package com.university.registration;

import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.Enrollment;
import com.university.registration.model.EnrollmentStatus;
import com.university.registration.model.Schedule;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentViewTest {

    @Test
    void weeklyScheduleAndEnrollmentSummaryReflectCurrentState() {
        Department department = new Department("COMP", "Computer Science");
        DegreeProgram program = new DegreeProgram("BSc CS", 120);
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        Course c1 = new Course("COMP248", "OOP I", 3, department);
        Course c2 = new Course("COMP249", "OOP II", 3, department);
        program.getCourses().add(c1);
        program.getCourses().add(c2);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, program);

        CourseSection s1 = new CourseSection("A", 2, c1, semester);
        s1.setSchedule(new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 15)));
        CourseSection s2 = new CourseSection("B", 2, c2, semester);
        s2.setSchedule(new Schedule(DayOfWeek.WEDNESDAY, LocalTime.of(12, 0), LocalTime.of(13, 15)));

        Enrollment e1 = new Enrollment(LocalDateTime.now(), EnrollmentStatus.ENROLLED, student, s1);
        Enrollment e2 = new Enrollment(LocalDateTime.now(), EnrollmentStatus.WAITLISTED, student, s2);
        student.getEnrollments().add(e1);
        student.getEnrollments().add(e2);
        s1.getEnrollments().add(e1);
        s2.getEnrollments().add(e2);
        s1.setEnrolledCount(1);

        List<String> weekly = student.viewWeeklySchedule(semester);
        String summary = student.getEnrollmentSummary(semester);

        assertEquals(1, weekly.size());
        assertTrue(weekly.get(0).contains("OOP I"));
        assertTrue(summary.contains("Enrolled: 1"));
        assertTrue(summary.contains("Waitlisted: 1"));
        assertTrue(summary.contains("Total Credits: 3"));
    }
}

