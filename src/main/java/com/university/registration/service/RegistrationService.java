package com.university.registration.service;

import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.Enrollment;
import com.university.registration.model.EnrollmentStatus;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegistrationService {

    public Enrollment enrollStudent(Student student, CourseSection section, Semester semester, LocalDate today) {
        if (!semester.isWithinRegistrationDate(today)) {
            throw new IllegalStateException("Registration window is closed. Deadline was: " + semester.getRegistrationEndDate());
        }
        if (section.getSemester() != semester) {
            throw new IllegalArgumentException("Section does not belong to the given semester.");
        }
        if (hasActiveEnrollmentInSection(student, section)) {
            throw new IllegalStateException("Student is already enrolled or waitlisted in this section.");
        }
        if (!isCourseAllowedForProgram(student, section.getCourse())) {
            throw new IllegalStateException("Course is not part of the student's degree program.");
        }
        List<Course> missingPrereqs = getMissingPrerequisites(student, section.getCourse());
        if (!missingPrereqs.isEmpty()) {
            String missing = missingPrereqs.stream().map(Course::getCourseCode).collect(Collectors.joining(", "));
            throw new IllegalStateException("Prerequisites not satisfied. Missing: " + missing);
        }
        int totalCredits = calculateCredits(student, semester);
        if (totalCredits + section.getCourse().getCredits() > student.getCreditLimit()) {
            throw new IllegalStateException("Credit limit exceeded.");
        }
        List<CourseSection> conflicts = getConflictingSections(student, section);
        if (!conflicts.isEmpty()) {
            String ids = conflicts.stream().map(CourseSection::getSectionId).collect(Collectors.joining(", "));
            throw new IllegalStateException("Schedule conflict detected with section(s): " + ids);
        }

        EnrollmentStatus status;
        if (section.isFull()) {
            section.updateWaitlist(student);
            status = EnrollmentStatus.WAITLISTED;
        } else {
            section.setEnrolledCount(section.getEnrolledCount() + 1);
            status = EnrollmentStatus.ENROLLED;
        }

        Enrollment enrollment = new Enrollment(LocalDateTime.now(), status, student, section);
        student.getEnrollments().add(enrollment);
        section.getEnrollments().add(enrollment);
        return enrollment;
    }

    public void dropCourse(Student student, CourseSection section, Semester semester, LocalDate today) {
        if (!semester.isWithinRegistrationDate(today)) {
            throw new IllegalStateException("Drop period is closed. Deadline was: " + semester.getRegistrationEndDate());
        }
        Optional<Enrollment> enrollmentOpt = student.getEnrollments().stream()
                .filter(e -> e.getSection().equals(section))
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.WAITLISTED)
                .findFirst();

        if (enrollmentOpt.isEmpty()) {
            throw new IllegalStateException("No active enrollment found for this section.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
            enrollment.setStatus(EnrollmentStatus.DROPPED);
            section.setEnrolledCount(Math.max(0, section.getEnrolledCount() - 1));
            promoteFromWaitlist(section);
        } else {
            enrollment.setStatus(EnrollmentStatus.DROPPED);
            section.getWaitlist().remove(student);
        }
    }

    public boolean validatePrerequisites(Student student, Course course) {
        return getMissingPrerequisites(student, course).isEmpty();
    }

    public boolean hasScheduleConflict(Student student, CourseSection section) {
        return !getConflictingSections(student, section).isEmpty();
    }

    public int calculateCredits(Student student, Semester semester) {
        return student.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .filter(e -> e.getSection().getSemester().equals(semester))
                .mapToInt(e -> e.getSection().getCourse().getCredits())
                .sum();
    }

    private void promoteFromWaitlist(CourseSection section) {
        int attemptsRemaining = section.getWaitlist().size();
        while (!section.isFull() && !section.getWaitlist().isEmpty() && attemptsRemaining > 0) {
            Student nextStudent = section.getWaitlist().poll();
            attemptsRemaining--;
            Optional<Enrollment> nextEnrollment = section.getEnrollments().stream()
                    .filter(e -> e.getStudent().equals(nextStudent))
                    .filter(e -> e.getStatus() == EnrollmentStatus.WAITLISTED)
                    .findFirst();
            if (nextEnrollment.isPresent()) {
                if (isEligibleForPromotion(nextStudent, section)) {
                    nextEnrollment.get().setStatus(EnrollmentStatus.ENROLLED);
                    section.setEnrolledCount(section.getEnrolledCount() + 1);
                } else {
                    // Skip for now and keep queue moving for others.
                    section.getWaitlist().offer(nextStudent);
                }
            }
        }
    }

    private boolean hasActiveEnrollmentInSection(Student student, CourseSection section) {
        return student.getEnrollments().stream().anyMatch(enrollment ->
                enrollment.getSection().equals(section)
                        && (enrollment.getStatus() == EnrollmentStatus.ENROLLED
                        || enrollment.getStatus() == EnrollmentStatus.WAITLISTED));
    }

    private boolean isCourseAllowedForProgram(Student student, Course course) {
        if (student.getDegreeProgram() == null || student.getDegreeProgram().getCourses().isEmpty()) {
            return true;
        }
        return student.getDegreeProgram().getCourses().contains(course);
    }

    public List<Course> getMissingPrerequisites(Student student, Course course) {
        return course.getPrerequisites().stream()
                .filter(prereq -> student.getEnrollments().stream().noneMatch(enrollment ->
                        enrollment.getSection().getCourse().equals(prereq)
                                && enrollment.getGrade() != null
                                && !enrollment.getGrade().equalsIgnoreCase("F")))
                .collect(Collectors.toList());
    }

    public List<CourseSection> getConflictingSections(Student student, CourseSection section) {
        return student.getConflictingSections(section);
    }

    private boolean isEligibleForPromotion(Student student, CourseSection section) {
        Semester semester = section.getSemester();
        LocalDate today = LocalDate.now();
        if (!semester.isWithinRegistrationDate(today)) {
            return false;
        }
        if (!isCourseAllowedForProgram(student, section.getCourse())) {
            return false;
        }
        if (!validatePrerequisites(student, section.getCourse())) {
            return false;
        }
        int totalCredits = calculateCredits(student, semester);
        if (totalCredits + section.getCourse().getCredits() > student.getCreditLimit()) {
            return false;
        }
        return !hasScheduleConflict(student, section);
    }
}
