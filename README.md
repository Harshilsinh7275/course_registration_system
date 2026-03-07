# Course Registration System (Java, Maven)

This project implements a UML-based Course Registration System using Java and Maven, with JUnit 5 tests and a terminal manual testing console.

## Features Implemented

- Student course browsing and searching by semester
- Enrollment with validation:
  - registration window (add/drop dates)
  - prerequisites
  - credit limit
  - schedule conflicts
  - duplicate enrollment prevention
  - degree program eligibility
- Waitlist handling and automatic promotion
- Course drop logic
- Instructor actions:
  - view assigned sections
  - view enrolled students in assigned section
  - assign grades
- Admin actions:
  - create course
  - create section
  - modify section fields
  - cancel section (before semester starts)
  - register student account
- Authentication:
  - login/logout
  - duplicate account prevention (ID/email/username)
  - role authorization helper
- Student weekly schedule and enrollment summary

## Tech Stack

- Java 17+ (tested with Java 23)
- Maven 3.9+
- JUnit 5

## Project Structure

```text
course-registration-system
├── pom.xml
└── src
    ├── main/java/com/university/registration
    │   ├── model
    │   ├── service
    │   ├── Main.java
    │   └── ManualTestConsole.java
    └── test/java/com/university/registration
```

## Prerequisites

Install and verify:

```bash
java -version
javac -version
mvn -version
```

If Maven is not recognized on Windows:

- Set `MAVEN_HOME` (example): `C:\apache-maven-3.9.13`
- Add `%MAVEN_HOME%\bin` to `Path`
- Open a new terminal

## Build and Run Tests

From project root:

```bash
mvn clean test
```

Current suite: 31 tests.

## Run Demo Main

```bash
java -cp target/classes com.university.registration.Main
```

## Manual Interactive Testing (Terminal)

Run:

```bash
mvn -DskipTests package
java -cp target/classes com.university.registration.ManualTestConsole
```

### Seeded Login Accounts

- Student: `alice` / `pass`
- Student: `bob` / `pass`
- Instructor: `prof` / `pass`
- Admin: `admin` / `pass`

### Student Flow (Menu)

- Browse/Search courses
- View course/section details
- Enroll/Drop
- Weekly schedule
- Enrollment summary

### Instructor Flow (Menu)

- View assigned sections
- View enrolled students in assigned section
- Add student grade

### Admin Flow (Menu)

- Create course
- Create section
- Set section capacity
- Register new student account

## Example Manual Test Cases

### Pass Example

Student (`alice`) enrolls into `COMP248-A` -> should return `ENROLLED`.

### Violation Example

Student (`alice`) enrolls into `COMP249-A` before completing `COMP248` -> should fail with prerequisite message.

## Notes

- This project is in-memory only (no GUI, no database).
- Business logic is in `RegistrationService`.
- Manual console is for role-based functional verification.

## Author

Add your name here.

