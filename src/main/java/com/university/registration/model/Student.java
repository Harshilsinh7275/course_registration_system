package com.university.registration.model;

import com.university.registration.service.RegistrationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Student extends Person {
    private int creditLimit;
    private DegreeProgram degreeProgram;
    private List<Enrollment> enrollments;

    private final RegistrationService registrationService = new RegistrationService();

    public Student(
            String id,
            String name,
            String email,
            String username,
            String password,
            int creditLimit,
            DegreeProgram degreeProgram
    ) {
        super(id, name, email, username, password);
        this.creditLimit = creditLimit;
        this.degreeProgram = degreeProgram;
        this.enrollments = new ArrayList<>();
    }

    public List<Course> browseCoursesBySemester(Semester semester) {
        return semester.getSections().stream()
                .map(CourseSection::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Course> searchCourses(Semester semester, String keyword) {
        String normalized = keyword.toLowerCase(Locale.ROOT);
        return browseCoursesBySemester(semester).stream()
                .filter(course -> course.getCourseCode().toLowerCase(Locale.ROOT).contains(normalized)
                        || course.getTitle().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    public String viewCourseDetails(Course course) {
        String prereqCodes = course.getPrerequisites().stream()
                .map(Course::getCourseCode)
                .sorted()
                .collect(Collectors.joining(", "));
        if (prereqCodes.isEmpty()) {
            prereqCodes = "None";
        }
        return course.getCourseCode()
                + " - " + course.getTitle()
                + " | Credits: " + course.getCredits()
                + " | Prerequisites: " + prereqCodes
                + " | Description: " + course.getDescription();
    }

    public String viewSectionDetails(CourseSection section) {
        String instructorName = section.getInstructor() != null ? section.getInstructor().getName() : "TBA";
        String dayTime = section.getSchedule() != null
                ? section.getSchedule().getDayOfWeek() + " " + section.getSchedule().getStartTime() + "-" + section.getSchedule().getEndTime()
                : "TBA";
        String room = section.getRoom() != null
                ? section.getRoom().getBuilding() + " " + section.getRoom().getRoomNumber()
                : "TBA";
        return section.getSectionId()
                + " | Instructor: " + instructorName
                + " | Schedule: " + dayTime
                + " | Location: " + room
                + " | Capacity: " + section.getCapacity()
                + " | Seats remaining: " + section.seatsRemaining()
                + " | State: " + section.refreshState(LocalDate.now());
    }

    public List<Enrollment> listEnrollments(Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getSection().getSemester().equals(semester))
                .collect(Collectors.toList());
    }

    public void enroll(CourseSection section, Semester semester) {
        registrationService.enrollStudent(this, section, semester, LocalDate.now());
    }

    public void drop(CourseSection section, Semester semester, LocalDate today) {
        registrationService.dropCourse(this, section, semester, today);
    }

    public int getTotalCredits(Semester semester) {
        return registrationService.calculateCredits(this, semester);
    }

    public boolean hasScheduleConflict(CourseSection section) {
        return !getConflictingSections(section).isEmpty();
    }

    public List<CourseSection> getConflictingSections(CourseSection section) {
        return enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .filter(e -> e.getSection().getSemester().equals(section.getSemester()))
                .map(Enrollment::getSection)
                .filter(existingSection -> {
                    Schedule current = existingSection.getSchedule();
                    Schedule incoming = section.getSchedule();
                    return current != null && incoming != null && current.overlapsRoom(incoming);
                })
                .collect(Collectors.toList());
    }

    public List<String> viewWeeklySchedule(Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .filter(e -> e.getSection().getSemester().equals(semester))
                .map(Enrollment::getSection)
                .filter(section -> section.getSchedule() != null)
                .sorted(Comparator
                        .comparing((CourseSection section) -> section.getSchedule().getDayOfWeek())
                        .thenComparing(section -> section.getSchedule().getStartTime()))
                .map(section -> {
                    String instructorName = section.getInstructor() != null ? section.getInstructor().getName() : "TBA";
                    Schedule current = section.getSchedule();
                    return section.getCourse().getTitle()
                            + " (" + section.getSectionId() + ")"
                            + " | " + current.getDayOfWeek()
                            + " " + current.getStartTime() + "-" + current.getEndTime()
                            + " | Instructor: " + instructorName;
                })
                .collect(Collectors.toList());
    }

    public String getEnrollmentSummary(Semester semester) {
        long enrolled = listEnrollments(semester).stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();
        long waitlisted = listEnrollments(semester).stream().filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED).count();
        return "Enrolled: " + enrolled + ", Waitlisted: " + waitlisted + ", Total Credits: " + getTotalCredits(semester);
    }

    public int getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}
