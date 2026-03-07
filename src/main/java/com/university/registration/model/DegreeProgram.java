package com.university.registration.model;

import java.util.ArrayList;
import java.util.List;

public class DegreeProgram {
    private String programName;
    private int minCredits;
    private List<Course> courses;

    public DegreeProgram(String programName, int minCredits) {
        this.programName = programName;
        this.minCredits = minCredits;
        this.courses = new ArrayList<>();
    }

    public List<Course> listCourses() {
        return new ArrayList<>(courses);
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getMinCredits() {
        return minCredits;
    }

    public void setMinCredits(int minCredits) {
        this.minCredits = minCredits;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}

