package com.university.registration.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Semester {
    private String semesterCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private List<CourseSection> sections;
    private SemesterState state;

    public Semester(
            String semesterCode,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate registrationStartDate,
            LocalDate registrationEndDate
    ) {
        this.semesterCode = semesterCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationStartDate = registrationStartDate;
        this.registrationEndDate = registrationEndDate;
        this.sections = new ArrayList<>();
        this.state = calculateState(LocalDate.now());
    }

    public boolean isWithinRegistrationDate(LocalDate today) {
        return (today.isEqual(registrationStartDate) || today.isAfter(registrationStartDate))
                && (today.isEqual(registrationEndDate) || today.isBefore(registrationEndDate));
    }

    public SemesterState getState() {
        return state;
    }

    public SemesterState refreshState(LocalDate today) {
        this.state = calculateState(today);
        return state;
    }

    private SemesterState calculateState(LocalDate today) {
        if (today.isBefore(registrationStartDate)) {
            return SemesterState.PLANNED;
        }
        if (!today.isAfter(registrationEndDate)) {
            return SemesterState.REGISTRATION_OPEN;
        }
        if (today.isBefore(startDate)) {
            return SemesterState.REGISTRATION_CLOSED;
        }
        if (!today.isAfter(endDate)) {
            return SemesterState.IN_PROGRESS;
        }
        return SemesterState.COMPLETED;
    }

    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDate registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDate getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(LocalDate registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }
}
