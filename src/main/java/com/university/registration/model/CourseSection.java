package com.university.registration.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CourseSection {
    private String sectionId;
    private int capacity;
    private int enrolledCount;
    private Queue<Student> waitlist;
    private int waitlistThreshold;
    private Course course;
    private Semester semester;
    private Instructor instructor;
    private Room room;
    private Schedule schedule;
    private List<Enrollment> enrollments;

    public CourseSection(String sectionId, int capacity, Course course, Semester semester) {
        this.sectionId = sectionId;
        this.capacity = capacity;
        this.course = course;
        this.semester = semester;
        this.waitlist = new LinkedList<>();
        this.waitlistThreshold = capacity * 2;
        this.enrollments = new ArrayList<>();
        this.enrolledCount = 0;
    }

    public int seatsRemaining() {
        return capacity - enrolledCount;
    }

    public boolean isFull() {
        return seatsRemaining() <= 0;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(semester.getStartDate()) && !today.isAfter(semester.getEndDate());
    }

    public void updateWaitlist(Student student) {
        if (!waitlist.contains(student) && (waitlistThreshold <= 0 || waitlist.size() < waitlistThreshold)) {
            waitlist.offer(student);
        }
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public Queue<Student> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(Queue<Student> waitlist) {
        this.waitlist = waitlist;
    }

    public int getWaitlistThreshold() {
        return waitlistThreshold;
    }

    public void setWaitlistThreshold(int waitlistThreshold) {
        this.waitlistThreshold = waitlistThreshold;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}

