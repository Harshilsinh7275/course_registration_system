package com.university.registration;

import com.university.registration.model.Admin;
import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.Enrollment;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Department department = new Department("COMP", "Computer Science");
        DegreeProgram degreeProgram = new DegreeProgram("BSc CS", 120);

        Course comp248 = new Course("COMP248", "Object-Oriented Programming I", 3, department);
        Course comp249 = new Course("COMP249", "Object-Oriented Programming II", 3, department);
        comp249.addPrerequisite(comp248);

        Admin admin = new Admin("A1", "Registrar Admin", "admin@university.com", "admin", "pass");
        admin.addCourse(comp248);
        admin.addCourse(comp249);

        Semester fall = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10)
        );

        CourseSection comp248SecA = new CourseSection("COMP248-A", 1, comp248, fall);
        CourseSection comp249SecA = new CourseSection("COMP249-A", 2, comp249, fall);
        admin.createSection(comp248, fall, comp248SecA);
        admin.createSection(comp249, fall, comp249SecA);

        Student student1 = new Student("S1", "Alice", "alice@u.com", "alice", "pass", 9, degreeProgram);
        Student student2 = new Student("S2", "Bob", "bob@u.com", "bob", "pass", 9, degreeProgram);

        student1.enroll(comp248SecA, fall);
        student2.enroll(comp248SecA, fall);

        printEnrollmentStatus(student1, "Alice");
        printEnrollmentStatus(student2, "Bob");
    }

    private static void printEnrollmentStatus(Student student, String studentName) {
        System.out.println("Enrollment results for " + studentName + ":");
        for (Enrollment enrollment : student.getEnrollments()) {
            System.out.println(" - " + enrollment.getSection().getSectionId() + ": " + enrollment.getStatus());
        }
    }
}

