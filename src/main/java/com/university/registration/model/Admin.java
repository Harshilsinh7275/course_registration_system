package com.university.registration.model;

import java.time.LocalDate;

public class Admin extends Person {
    public Admin(String id, String name, String email, String username, String password) {
        super(id, name, email, username, password);
    }

    public void addCourse(Course course) {
        if (course.getDepartment() != null && !course.getDepartment().getCourses().contains(course)) {
            course.getDepartment().getCourses().add(course);
        }
    }

    public void updateCourse(Course course) {
        addCourse(course);
    }

    public void createSection(Course course, Semester semester, CourseSection section) {
        if (!course.getSections().contains(section)) {
            course.getSections().add(section);
        }
        if (!semester.getSections().contains(section)) {
            semester.getSections().add(section);
        }
    }

    public Course createCourse(String courseCode, String title, String description, int credits, Department department) {
        Course course = new Course(courseCode, title, description, credits, department);
        addCourse(course);
        return course;
    }

    public void assignInstructor(CourseSection section, Instructor instructor) {
        section.setInstructor(instructor);
    }

    public void assignRoom(CourseSection section, Room room) {
        section.setRoom(room);
    }

    public void setSchedule(CourseSection section, Schedule schedule) {
        section.setSchedule(schedule);
    }

    public void setCapacity(CourseSection section, int capacity) {
        section.setCapacity(capacity);
    }

    public void createSemester(Semester semester) {
        // Kept intentionally simple for in-memory demo.
    }

    public void modifySection(CourseSection section, Instructor instructor, Room room, Schedule schedule, int capacity) {
        ensureBeforeSemesterStart(section.getSemester());
        assignInstructor(section, instructor);
        assignRoom(section, room);
        setSchedule(section, schedule);
        setCapacity(section, capacity);
    }

    public void cancelSection(Course course, Semester semester, CourseSection section) {
        ensureBeforeSemesterStart(semester);
        section.cancel(LocalDate.now());
        course.getSections().remove(section);
        semester.getSections().remove(section);
    }

    private void ensureBeforeSemesterStart(Semester semester) {
        if (semester.refreshState(LocalDate.now()) == SemesterState.IN_PROGRESS
                || semester.refreshState(LocalDate.now()) == SemesterState.COMPLETED) {
            throw new IllegalStateException("Section changes are allowed only before semester start.");
        }
    }
}
