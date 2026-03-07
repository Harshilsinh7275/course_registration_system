package com.university.registration;

import com.university.registration.model.Admin;
import com.university.registration.model.Authenticator;
import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.Enrollment;
import com.university.registration.model.Instructor;
import com.university.registration.model.Person;
import com.university.registration.model.Schedule;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;
import com.university.registration.service.RegistrationService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ManualTestConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final RegistrationService registrationService = new RegistrationService();

    private Department department;
    private DegreeProgram degreeProgram;
    private Semester semester;
    private final List<Course> courses = new ArrayList<>();
    private final List<CourseSection> sections = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private Instructor instructor;
    private Admin admin;
    private Authenticator authenticator;

    public static void main(String[] args) {
        new ManualTestConsole().run();
    }

    private void run() {
        seedData();
        System.out.println("Course Registration - Manual Test Console");
        while (true) {
            System.out.println("\nLogin as: 1) Student 2) Instructor 3) Admin 0) Exit");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> studentSession();
                case "2" -> instructorSession();
                case "3" -> adminSession();
                case "0" -> {
                    System.out.println("Bye.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void seedData() {
        LocalDate today = LocalDate.now();
        department = new Department("COMP", "Computer Science");
        degreeProgram = new DegreeProgram("BSc CS", 120);
        semester = new Semester(
                "FALL-TEST",
                today.plusDays(30),
                today.plusDays(120),
                today.minusDays(10),
                today.plusDays(20)
        );

        Course comp248 = new Course("COMP248", "OOP I", "Intro to OOP", 3, department);
        Course comp249 = new Course("COMP249", "OOP II", "Advanced OOP", 3, department);
        comp249.addPrerequisite(comp248);
        degreeProgram.getCourses().add(comp248);
        degreeProgram.getCourses().add(comp249);
        courses.add(comp248);
        courses.add(comp249);

        CourseSection s248a = new CourseSection("COMP248-A", 1, comp248, semester);
        s248a.setSchedule(new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 15)));
        CourseSection s249a = new CourseSection("COMP249-A", 2, comp249, semester);
        s249a.setSchedule(new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 45)));

        courses.forEach(c -> c.getSections().clear());
        comp248.getSections().add(s248a);
        comp249.getSections().add(s249a);
        semester.getSections().clear();
        semester.getSections().add(s248a);
        semester.getSections().add(s249a);
        sections.add(s248a);
        sections.add(s249a);

        Student alice = new Student("S1", "Alice", "alice@u.com", "alice", "pass", 9, degreeProgram);
        Student bob = new Student("S2", "Bob", "bob@u.com", "bob", "pass", 9, degreeProgram);
        students.add(alice);
        students.add(bob);

        instructor = new Instructor("I1", "Prof", "prof@u.com", "prof", "pass");
        admin = new Admin("A1", "Admin", "admin@u.com", "admin", "pass");
        sections.forEach(s -> s.setInstructor(instructor));

        List<Person> users = new ArrayList<>();
        users.addAll(students);
        users.add(instructor);
        users.add(admin);
        authenticator = new Authenticator(users);
    }

    private void studentSession() {
        Student student = loginStudent();
        if (student == null) {
            return;
        }
        while (true) {
            System.out.println("\nStudent Menu");
            System.out.println("1) Browse courses");
            System.out.println("2) Search courses");
            System.out.println("3) View course details");
            System.out.println("4) View section details");
            System.out.println("5) Enroll");
            System.out.println("6) Drop");
            System.out.println("7) Weekly schedule");
            System.out.println("8) Enrollment summary");
            System.out.println("0) Logout");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> student.browseCoursesBySemester(semester)
                            .forEach(c -> System.out.println(c.getCourseCode() + " - " + c.getTitle()));
                    case "2" -> {
                        System.out.print("Keyword: ");
                        String keyword = scanner.nextLine();
                        student.searchCourses(semester, keyword)
                                .forEach(c -> System.out.println(c.getCourseCode() + " - " + c.getTitle()));
                    }
                    case "3" -> {
                        Course c = findCourseByCode(prompt("Course code: "));
                        if (c != null) {
                            System.out.println(student.viewCourseDetails(c));
                        }
                    }
                    case "4" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        if (section != null) {
                            System.out.println(student.viewSectionDetails(section));
                        }
                    }
                    case "5" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        if (section != null) {
                            Enrollment e = registrationService.enrollStudent(student, section, semester, LocalDate.now());
                            System.out.println("Result: " + e.getStatus());
                        }
                    }
                    case "6" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        if (section != null) {
                            registrationService.dropCourse(student, section, semester, LocalDate.now());
                            System.out.println("Drop successful.");
                        }
                    }
                    case "7" -> student.viewWeeklySchedule(semester).forEach(System.out::println);
                    case "8" -> System.out.println(student.getEnrollmentSummary(semester));
                    case "0" -> {
                        authenticator.logout(student);
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private void instructorSession() {
        Person person = login("Instructor username: ", "Password: ");
        if (!(person instanceof Instructor ins)) {
            System.out.println("Not an instructor account.");
            return;
        }
        while (true) {
            System.out.println("\nInstructor Menu");
            System.out.println("1) View assigned sections");
            System.out.println("2) View enrolled students in section");
            System.out.println("3) Add student grade");
            System.out.println("0) Logout");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> ins.viewAssignedSections(semester)
                            .forEach(s -> System.out.println(s.getSectionId()));
                    case "2" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        if (section != null) {
                            ins.viewEnrolledStudents(section).forEach(e ->
                                    System.out.println(e.getStudent().getId() + " | " + e.getStudent().getName() + " | " + e.getStatus()));
                        }
                    }
                    case "3" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        String studentId = prompt("Student id: ").toUpperCase(Locale.ROOT);
                        Student student = students.stream()
                                .filter(s -> s.getId().toUpperCase(Locale.ROOT).equals(studentId))
                                .findFirst()
                                .orElse(null);
                        if (section != null && student != null) {
                            ins.addStudentGrade(section, student, prompt("Grade: "));
                            System.out.println("Grade updated.");
                        }
                    }
                    case "0" -> {
                        authenticator.logout(ins);
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private void adminSession() {
        Person person = login("Admin username: ", "Password: ");
        if (!(person instanceof Admin ad)) {
            System.out.println("Not an admin account.");
            return;
        }
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1) Create course");
            System.out.println("2) Create section");
            System.out.println("3) Set section capacity");
            System.out.println("4) Register new student account");
            System.out.println("0) Logout");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        String code = prompt("Course code: ");
                        String title = prompt("Title: ");
                        String description = prompt("Description: ");
                        int credits = Integer.parseInt(prompt("Credits: "));
                        Course c = ad.createCourse(code, title, description, credits, department);
                        degreeProgram.getCourses().add(c);
                        courses.add(c);
                        System.out.println("Created: " + c.getCourseCode());
                    }
                    case "2" -> {
                        Course c = findCourseByCode(prompt("Course code: "));
                        if (c != null) {
                            String sectionId = prompt("Section id: ");
                            int cap = Integer.parseInt(prompt("Capacity: "));
                            CourseSection section = new CourseSection(sectionId, cap, c, semester);
                            ad.createSection(c, semester, section);
                            sections.add(section);
                            System.out.println("Section created.");
                        }
                    }
                    case "3" -> {
                        CourseSection section = findSectionById(prompt("Section id: "));
                        if (section != null) {
                            ad.setCapacity(section, Integer.parseInt(prompt("New capacity: ")));
                            System.out.println("Capacity updated.");
                        }
                    }
                    case "4" -> {
                        Student s = new Student(
                                prompt("ID: "),
                                prompt("Name: "),
                                prompt("Email: "),
                                prompt("Username: "),
                                prompt("Password: "),
                                Integer.parseInt(prompt("Credit limit: ")),
                                degreeProgram
                        );
                        authenticator.registerUser(s);
                        students.add(s);
                        System.out.println("Student account registered.");
                    }
                    case "0" -> {
                        authenticator.logout(ad);
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private Student loginStudent() {
        Person user = login("Student username: ", "Password: ");
        if (user instanceof Student student) {
            return student;
        }
        System.out.println("Not a student account.");
        return null;
    }

    private Person login(String uPrompt, String pPrompt) {
        String username = prompt(uPrompt);
        String password = prompt(pPrompt);
        Person user = authenticator.login(username, password);
        if (user == null) {
            System.out.println("Invalid credentials.");
        }
        return user;
    }

    private Course findCourseByCode(String code) {
        String key = code.trim().toUpperCase(Locale.ROOT);
        Course c = courses.stream()
                .filter(course -> course.getCourseCode().toUpperCase(Locale.ROOT).equals(key))
                .findFirst()
                .orElse(null);
        if (c == null) {
            System.out.println("Course not found.");
        }
        return c;
    }

    private CourseSection findSectionById(String id) {
        String key = id.trim().toUpperCase(Locale.ROOT);
        CourseSection s = sections.stream()
                .filter(section -> section.getSectionId().toUpperCase(Locale.ROOT).equals(key))
                .findFirst()
                .orElse(null);
        if (s == null) {
            System.out.println("Section not found.");
        }
        return s;
    }

    private String prompt(String text) {
        System.out.print(text);
        return scanner.nextLine().trim();
    }
}

