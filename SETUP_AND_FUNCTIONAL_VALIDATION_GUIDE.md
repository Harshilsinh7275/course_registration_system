# Setup and Functional Validation Guide

This file explains exactly how to:

1. Install required tools (Java + Maven)  
2. Build and run the project  
3. Execute automated tests  
4. Execute manual role-based testing from terminal  
5. Verify functional requirements and UML alignment

---

## 1) Prerequisites

### 1.1 Install Java (JDK 17+)

- Recommended: JDK 17 or above
- Verify:

```cmd
java -version
javac -version
```

### 1.2 Install Maven (3.9+)

- Download binary zip from Apache Maven
- Extract (example): `C:\apache-maven-3.9.13`
- Set environment variables:
  - `MAVEN_HOME=C:\apache-maven-3.9.13`
  - Add `%MAVEN_HOME%\bin` to `Path`
- Verify:

```cmd
mvn -version
```

---

## 2) Clone and Build

```cmd
git clone https://github.com/Harshilsinh7275/course_registration_system.git
cd course_registration_system
mvn clean test
```

Expected: `BUILD SUCCESS`

---

## 3) Run Demo Program

```cmd
java -cp target\classes com.university.registration.Main
```

This demonstrates a sample enrollment + waitlist scenario.

---

## 4) Automated Test Execution

### 4.1 Run all tests

```cmd
mvn test
```

Expected current result: **31 tests, 0 failures, 0 errors**

### 4.2 Run selected classes

```cmd
mvn -Dtest="RegistrationRulesTest,AdminTest,InstructorTest,AuthenticatorTest,StudentViewTest" test
```

### 4.3 Run a single test method

```cmd
mvn -Dtest=StudentTest#successfulEnrollment test
```

### 4.4 Test reports

Reports are generated in:

`target\surefire-reports`

---

## 5) Manual Interactive Testing (Role-Based)

Run:

```cmd
mvn -DskipTests package
java -cp target\classes com.university.registration.ManualTestConsole
```

### Seeded users

- Student: `alice` / `pass`
- Student: `bob` / `pass`
- Instructor: `prof` / `pass`
- Admin: `admin` / `pass`

### 5.1 Student role tests

- Browse/search courses
- View course details
- View section details
- Enroll in section
- Drop section
- View weekly schedule
- View enrollment summary

Example pass:
- Login as `alice`
- Enroll `COMP248-A`
- Expected: `ENROLLED`

Example violation:
- Login as `alice`
- Enroll `COMP249-A` before passing prerequisite
- Expected: failure with prerequisite message

### 5.2 Instructor role tests

- View assigned sections
- View enrolled students for assigned section
- Add student grade

Violation example:
- Instructor tries viewing students for non-assigned section
- Expected: restricted access error

### 5.3 Admin role tests

- Create course
- Create section
- Set capacity
- Register student account
- Modify/cancel section only before semester start

Violation example:
- Register duplicate account (same ID/email/username)
- Expected: duplicate account error

---

## 6) Functional Requirements Coverage Summary

Status key:
- `Covered` = implemented + tested
- `Partially Covered` = implemented, but simplified behavior

1. FR-1 Course Browsing: **Covered**
2. FR-2 Enrollment: **Covered**
3. FR-3 Drop Course: **Covered**
4. FR-4 Waitlist: **Covered**
5. FR-5 Automatic Waitlist Promotion: **Covered** (includes re-check/skip behavior)
6. FR-6 Prerequisite Validation: **Covered** (missing prerequisite shown)
7. FR-7 Schedule Conflict: **Covered** (conflicting section(s) shown)
8. FR-8 Credit Limit Validation: **Covered**
9. FR-9 Duplicate Enrollment: **Covered**
10. FR-10 Add/Drop Deadline Enforcement: **Covered** (deadline date shown in error)
11. FR-11 Eligibility Re-check on Promotion: **Covered**
12. FR-12 Weekly Schedule View: **Covered**
13. FR-13 Enrollment Summary: **Covered**
14. FR-14 Instructor View of Students: **Covered** (own sections only)
15. FR-15 Course Creation (Admin): **Covered**
16. FR-16 Section Management (Admin): **Covered** (create/modify/cancel before start)
17. FR-17 Account Registration: **Covered** (duplicate prevention included)
18. FR-18 Login Authentication: **Covered** (credential validation + role authorization helper)

---

## 7) UML Conformance Check

### 7.1 Core UML classes and relationships

All core UML entities are present and used:

- `Person` (abstract), `Student`, `Instructor`, `Admin`
- `Department`, `DegreeProgram`, `Course`, `Semester`
- `CourseSection`, `Room`, `Schedule`, `Enrollment`
- `Authenticator`, `EnrollmentStatus`
- `RegistrationService` (service layer)

Associations (student-enrollment-section, course-prerequisites, section-instructor-room-schedule, semester-sections) are implemented.

### 7.2 Additive (non-breaking) extensions

To support full functional requirement testing, a few helper additions were introduced (without removing UML-defined members), such as:

- `Course.description`
- utility methods for summaries/weekly view/reason messages
- `ManualTestConsole` for terminal-based role testing

These do not break UML core structure and are additive for usability/testing.

---

## 8) Final Verification Checklist

Run in order:

```cmd
mvn clean test
java -cp target\classes com.university.registration.Main
java -cp target\classes com.university.registration.ManualTestConsole
```

If all pass and manual scenarios behave as expected, the system is validated against required functionality.

