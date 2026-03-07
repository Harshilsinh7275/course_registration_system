package com.university.registration;

import com.university.registration.model.Course;
import com.university.registration.model.CourseSection;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Department;
import com.university.registration.model.Enrollment;
import com.university.registration.model.EnrollmentStatus;
import com.university.registration.model.Semester;
import com.university.registration.model.Student;
import com.university.registration.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationRulesTest {

    private Department department;
    private DegreeProgram degreeProgram;
    private Semester semester;
    private RegistrationService service;

    @BeforeEach
    void setUp() {
        department = new Department("COMP", "Computer Science");
        degreeProgram = new DegreeProgram("BSc CS", 120);
        semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );
        service = new RegistrationService();
    }

    @Test
    void registrationBoundaryDatesAreInclusive() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        CourseSection section = new CourseSection("A", 2, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        assertDoesNotThrow(() -> service.enrollStudent(student, section, semester, LocalDate.of(2026, 7, 1)));

        Student student2 = new Student("S2", "Bob", "b@u.com", "bob", "pass", 9, degreeProgram);
        assertDoesNotThrow(() -> service.enrollStudent(student2, section, semester, LocalDate.of(2026, 8, 31)));
    }

    @Test
    void cannotEnrollOutsideRegistrationWindow() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        CourseSection section = new CourseSection("A", 2, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, section, semester, LocalDate.of(2026, 6, 30)));
    }

    @Test
    void cannotEnrollInCourseOutsideDegreeProgramWhenProgramCoursesDefined() {
        Course allowed = new Course("COMP248", "OOP I", 3, department);
        Course disallowed = new Course("COMP233", "Probability", 3, department);
        degreeProgram.getCourses().add(allowed);
        CourseSection section = new CourseSection("B", 2, disallowed, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);

        assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, section, semester, LocalDate.of(2026, 7, 15)));
    }

    @Test
    void duplicateEnrollmentInSameSectionIsRejected() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        CourseSection section = new CourseSection("A", 2, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        service.enrollStudent(student, section, semester, LocalDate.of(2026, 7, 10));

        assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, section, semester, LocalDate.of(2026, 7, 10)));
    }

    @Test
    void prerequisiteMustBeCompletedWithPassingGrade() {
        Course prereq = new Course("COMP248", "OOP I", 3, department);
        Course target = new Course("COMP249", "OOP II", 3, department);
        target.addPrerequisite(prereq);
        degreeProgram.getCourses().add(prereq);
        degreeProgram.getCourses().add(target);

        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);
        CourseSection prereqSection = new CourseSection("P1", 2, prereq, semester);
        Enrollment completed = new Enrollment(LocalDateTime.now().minusMonths(1), EnrollmentStatus.ENROLLED, student, prereqSection);
        completed.setGrade("A-");
        student.getEnrollments().add(completed);

        CourseSection targetSection = new CourseSection("T1", 2, target, semester);
        assertDoesNotThrow(() -> service.enrollStudent(student, targetSection, semester, LocalDate.of(2026, 7, 20)));
    }

    @Test
    void prerequisiteWithFailingGradeDoesNotAllowEnrollment() {
        Course prereq = new Course("COMP248", "OOP I", 3, department);
        Course target = new Course("COMP249", "OOP II", 3, department);
        target.addPrerequisite(prereq);
        degreeProgram.getCourses().add(prereq);
        degreeProgram.getCourses().add(target);

        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);
        CourseSection prereqSection = new CourseSection("P1", 2, prereq, semester);
        Enrollment failed = new Enrollment(LocalDateTime.now().minusMonths(1), EnrollmentStatus.ENROLLED, student, prereqSection);
        failed.setGrade("F");
        student.getEnrollments().add(failed);

        CourseSection targetSection = new CourseSection("T1", 2, target, semester);
        assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, targetSection, semester, LocalDate.of(2026, 7, 20)));
    }

    @Test
    void dropWaitlistedEnrollmentRemovesStudentFromQueue() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        CourseSection section = new CourseSection("A", 1, course, semester);

        Student s1 = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);
        Student s2 = new Student("S2", "Bob", "b@u.com", "bob", "pass", 9, degreeProgram);

        service.enrollStudent(s1, section, semester, LocalDate.of(2026, 7, 10));
        service.enrollStudent(s2, section, semester, LocalDate.of(2026, 7, 10));
        assertEquals(1, section.getWaitlist().size());

        service.dropCourse(s2, section, semester, LocalDate.of(2026, 7, 11));
        assertEquals(0, section.getWaitlist().size());
        assertEquals(EnrollmentStatus.DROPPED, s2.getEnrollments().get(0).getStatus());
    }

    @Test
    void sectionSemesterMismatchIsRejected() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        Semester winter = new Semester(
                "WINTER-2027",
                LocalDate.of(2027, 1, 10),
                LocalDate.of(2027, 4, 25),
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2027, 1, 5)
        );
        CourseSection winterSection = new CourseSection("W1", 2, course, winter);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);

        assertThrows(IllegalArgumentException.class,
                () -> service.enrollStudent(student, winterSection, semester, LocalDate.of(2026, 7, 20)));
    }

    @Test
    void prerequisiteFailureMessageIncludesMissingCourseCode() {
        Course prereq = new Course("COMP248", "OOP I", 3, department);
        Course target = new Course("COMP249", "OOP II", 3, department);
        target.addPrerequisite(prereq);
        degreeProgram.getCourses().add(target);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);
        CourseSection targetSection = new CourseSection("T1", 2, target, semester);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, targetSection, semester, LocalDate.of(2026, 7, 20)));
        assertTrue(ex.getMessage().contains("Missing: COMP248"));
    }

    @Test
    void scheduleConflictMessageIncludesConflictingSectionId() {
        Course c1 = new Course("COMP248", "OOP I", 3, department);
        Course c2 = new Course("COMP249", "OOP II", 3, department);
        degreeProgram.getCourses().add(c1);
        degreeProgram.getCourses().add(c2);

        CourseSection s1 = new CourseSection("A", 2, c1, semester);
        s1.setSchedule(new com.university.registration.model.Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        CourseSection s2 = new CourseSection("B", 2, c2, semester);
        s2.setSchedule(new com.university.registration.model.Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, degreeProgram);

        service.enrollStudent(student, s1, semester, LocalDate.of(2026, 7, 20));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, s2, semester, LocalDate.of(2026, 7, 20)));
        assertTrue(ex.getMessage().contains("A"));
    }

    @Test
    void waitlistPromotionSkipsIneligibleStudentAndPromotesNext() {
        Semester activeSemester = new Semester(
                "ACTIVE",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(90),
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10)
        );
        Course mainCourse = new Course("COMP352", "Data Structures", 3, department);
        Course heavyCourse = new Course("COMP400", "Advanced Topics", 6, department);
        degreeProgram.getCourses().add(mainCourse);
        degreeProgram.getCourses().add(heavyCourse);

        CourseSection mainSection = new CourseSection("MAIN", 1, mainCourse, activeSemester);
        CourseSection heavySection = new CourseSection("HEAVY", 2, heavyCourse, activeSemester);
        Student s1 = new Student("S1", "Enrolled", "s1@u.com", "s1", "pass", 12, degreeProgram);
        Student s2 = new Student("S2", "WillBeIneligible", "s2@u.com", "s2", "pass", 6, degreeProgram);
        Student s3 = new Student("S3", "Promoted", "s3@u.com", "s3", "pass", 12, degreeProgram);

        service.enrollStudent(s1, mainSection, activeSemester, LocalDate.now());
        service.enrollStudent(s2, mainSection, activeSemester, LocalDate.now());
        service.enrollStudent(s3, mainSection, activeSemester, LocalDate.now());

        // Make first waitlisted student ineligible before promotion by maxing out credits.
        Enrollment heavyEnrollment = new Enrollment(LocalDateTime.now(), EnrollmentStatus.ENROLLED, s2, heavySection);
        s2.getEnrollments().add(heavyEnrollment);
        heavySection.getEnrollments().add(heavyEnrollment);
        heavySection.setEnrolledCount(1);

        service.dropCourse(s1, mainSection, activeSemester, LocalDate.now());

        Enrollment s2Main = s2.getEnrollments().stream()
                .filter(e -> e.getSection().equals(mainSection))
                .findFirst()
                .orElseThrow();
        Enrollment s3Main = s3.getEnrollments().stream()
                .filter(e -> e.getSection().equals(mainSection))
                .findFirst()
                .orElseThrow();

        assertEquals(EnrollmentStatus.WAITLISTED, s2Main.getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, s3Main.getStatus());
    }

    @Test
    void deadlineMessageContainsConfiguredDate() {
        Course course = new Course("COMP248", "OOP I", 3, department);
        degreeProgram.getCourses().add(course);
        CourseSection section = new CourseSection("A", 2, course, semester);
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 9, degreeProgram);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.enrollStudent(student, section, semester, LocalDate.of(2026, 9, 1)));
        assertTrue(ex.getMessage().contains("2026-08-31"));
    }
}
