package com.university.registration.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String deptCode;
    private String name;
    private List<Course> courses;
    private List<Instructor> instructors;

    public Department(String deptCode, String name) {
        this.deptCode = deptCode;
        this.name = name;
        this.courses = new ArrayList<>();
        this.instructors = new ArrayList<>();
    }

    public List<Course> listCourses() {
        return new ArrayList<>(courses);
    }

    public List<Instructor> listInstructors() {
        return new ArrayList<>(instructors);
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Instructor> instructors) {
        this.instructors = instructors;
    }
}

