package com.university.registration;

import com.university.registration.model.Admin;
import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.Department;
import com.university.registration.model.Instructor;
import com.university.registration.model.Room;
import com.university.registration.model.Schedule;
import com.university.registration.model.Semester;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminTest {

    @Test
    void adminCanCreateAndAssignSectionDetails() {
        Admin admin = new Admin("A1", "Admin", "admin@u.com", "admin", "pass");
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
        Instructor instructor = new Instructor("I1", "Prof", "prof@u.com", "prof", "pass");
        Room room = new Room("EV", "1.101", 80);
        Schedule schedule = new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 15));

        admin.addCourse(course);
        admin.createSection(course, semester, section);
        admin.assignInstructor(section, instructor);
        admin.assignRoom(section, room);
        admin.setSchedule(section, schedule);
        admin.setCapacity(section, 40);

        assertTrue(department.getCourses().contains(course));
        assertTrue(course.getSections().contains(section));
        assertTrue(semester.getSections().contains(section));
        assertEquals(instructor, section.getInstructor());
        assertEquals(room, section.getRoom());
        assertEquals(schedule, section.getSchedule());
        assertEquals(40, section.getCapacity());
    }

    @Test
    void adminCanCreateCourseWithDescriptionAndCancelSection() {
        Admin admin = new Admin("A1", "Admin", "admin@u.com", "admin", "pass");
        Department department = new Department("COMP", "Computer Science");
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        Course course = admin.createCourse("COMP352", "Data Structures", "Core data structures and complexity.", 3, department);
        CourseSection section = new CourseSection("A", 30, course, semester);
        admin.createSection(course, semester, section);

        assertEquals("Core data structures and complexity.", course.getDescription());
        assertTrue(semester.getSections().contains(section));

        admin.cancelSection(course, semester, section);
        assertTrue(!semester.getSections().contains(section));
        assertTrue(!course.getSections().contains(section));
    }

    @Test
    void adminCannotModifyOrCancelSectionAfterSemesterStarts() {
        Admin admin = new Admin("A1", "Admin", "admin@u.com", "admin", "pass");
        Department department = new Department("COMP", "Computer Science");
        Course course = new Course("COMP248", "OOP I", 3, department);
        Semester startedSemester = new Semester(
                "PAST-SEM",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30),
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(2)
        );
        CourseSection section = new CourseSection("A", 30, course, startedSemester);
        admin.createSection(course, startedSemester, section);

        assertThrows(IllegalStateException.class,
                () -> admin.modifySection(section, null, null, null, 20));
        assertThrows(IllegalStateException.class,
                () -> admin.cancelSection(course, startedSemester, section));
    }
}
