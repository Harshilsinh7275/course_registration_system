package com.university.registration.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Course {
    private String courseCode;
    private String title;
    private String description;
    private int credits;
    private Set<Course> prerequisites;
    private List<CourseSection> sections;
    private Department department;

    public Course(String courseCode, String title, int credits, Department department) {
        this(courseCode, title, "", credits, department);
    }

    public Course(String courseCode, String title, String description, int credits, Department department) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.credits = credits;
        this.department = department;
        this.prerequisites = new HashSet<>();
        this.sections = new ArrayList<>();
    }

    public Set<Course> getPrerequisites() {
        return prerequisites;
    }

    public void addPrerequisite(Course course) {
        prerequisites.add(course);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course course)) {
            return false;
        }
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }
}
