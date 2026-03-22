# OCL (Object Constraint Language) Design for Course Registration System

## Progressive Guide: Simple to Complex

This document progressively designs OCL constraints for each concept in the course registration system, starting from the simplest to the most complex.

---

## 1. ROOM - Simplest Concept

**Concept:** Physical room where classes are held.
**Attributes:** building, roomNumber, capacity

### Invariants:

```ocl
-- Room must have positive capacity
context Room
inv positiveCapacity: self.capacity > 0

-- Building name must not be empty
context Room
inv buildingNameNotEmpty: self.building <> null and self.building.size() > 0

-- Room number must not be empty
context Room
inv roomNumberNotEmpty: self.roomNumber <> null and self.roomNumber.size() > 0
```

### Operations:

```ocl
-- Constructor validation
context Room::Room(building: String, roomNumber: String, capacity: int)
pre: building <> null and roomNumber <> null and capacity > 0
post: self.building = building and self.roomNumber = roomNumber and self.capacity = capacity

-- Capacity setter validation
context Room::setCapacity(capacity: int)
pre: capacity > 0
post: self.capacity = capacity
```

---

## 2. SCHEDULE - Simple Time-Based Concept

**Concept:** Class schedule with day, start time, and end time.
**Attributes:** dayOfWeek, startTime, endTime

### Invariants:

```ocl
-- Start time must be before end time
context Schedule
inv validTimeRange: self.startTime < self.endTime

-- Day of week must not be null
context Schedule
inv dayOfWeekNotNull: self.dayOfWeek <> null

-- Times must not be null
context Schedule
inv timesNotNull: self.startTime <> null and self.endTime <> null

-- Class duration must be reasonable (e.g., at least 30 minutes, max 4 hours)
context Schedule
inv reasonableDuration:
    let duration = self.endTime.toMinute() - self.startTime.toMinute() in
    duration >= 30 and duration <= 240
```

### Operations:

```ocl
-- Check if two schedules overlap on same day
context Schedule::overlapsRoom(other: Schedule): Boolean
pre: other <> null
post:
    if self.dayOfWeek <> other.dayOfWeek then
        result = false
    else
        result = self.startTime < other.endTime and other.startTime < self.endTime
    endif

-- Constructor validation
context Schedule::Schedule(dayOfWeek: DayOfWeek, startTime: LocalTime, endTime: LocalTime)
pre: dayOfWeek <> null and startTime <> null and endTime <> null and startTime < endTime
post: self.dayOfWeek = dayOfWeek and self.startTime = startTime and self.endTime = endTime
```

---

## 3. PERSON - Base Class with Authentication

**Concept:** Abstract base class for all users (Student, Instructor, Admin).
**Attributes:** id, name, email, username, password, loggedIn

### Invariants:

```ocl
-- ID must not be empty
context Person
inv idNotEmpty: self.id <> null and self.id.size() > 0

-- Name must not be empty
context Person
inv nameNotEmpty: self.name <> null and self.name.size() > 0

-- Email format (contains @ symbol)
context Person
inv emailFormat: self.email <> null and self.email.indexOf('@') > 0

-- Username must not be empty and at least 3 characters
context Person
inv usernameValid: self.username <> null and self.username.size() >= 3

-- Password must not be empty and at least 6 characters (security)
context Person
inv passwordSecure: self.password <> null and self.password.size() >= 6

-- If not logged in, loggedIn must be false
context Person
inv initialLogoutState: self.loggedIn = false
```

### Operations:

```ocl
-- Login only succeeds if credentials match
context Person::login(username: String, password: String): Boolean
pre: username <> null and password <> null
post:
    if self.username = username and self.password = password then
        result = true and self.loggedIn = true
    else
        result = false and self.loggedIn = false
    endif

-- Logout always succeeds
context Person::logout(): void
pre: true
post: self.loggedIn = false

-- Constructor validation
context Person::Person(id: String, name: String, email: String, username: String, password: String)
pre:
    id <> null and id.size() > 0 and
    name <> null and name.size() > 0 and
    email <> null and email.indexOf('@') > 0 and
    username <> null and username.size() >= 3 and
    password <> null and password.size() >= 6
post:
    self.id = id and self.name = name and self.email = email and
    self.username = username and self.password = password and
    self.loggedIn = false
```

---

## 4. SEMESTER - Date-Based Constraints

**Concept:** Academic period with registration dates.
**Attributes:** semesterCode, startDate, endDate, registrationStartDate, registrationEndDate, sections

### Invariants:

```ocl
-- Semester code must not be empty
context Semester
inv semesterCodeNotEmpty: self.semesterCode <> null and self.semesterCode.size() > 0

-- Start date must be before end date
context Semester
inv semesterDateRange: self.startDate < self.endDate

-- Registration start date must be before semester start
context Semester
inv registrationBeforeSemestStart: self.registrationStartDate <= self.startDate

-- Registration end date must be before or equal to semester end date
context Semester
inv registrationBeforeSemesterEnd: self.registrationEndDate <= self.endDate

-- Registration start must be before registration end
context Semester
inv registrationDateRange: self.registrationStartDate < self.registrationEndDate

-- All dates must be within reasonable academic year
context Semester
inv durationNotEmpty: self.endDate.year() - self.startDate.year() <= 1

-- Sections list should not be null
context Semester
inv sectionsNotNull: self.sections <> null
```

### Operations and Query Functions:

```ocl
-- Check if today is within registration window
context Semester::isWithinRegistrationDate(today: LocalDate): Boolean
pre: today <> null
post:
    result = (today >= self.registrationStartDate and today <= self.registrationEndDate)

-- Constructor validation
context Semester::Semester(
    semesterCode: String,
    startDate: LocalDate,
    endDate: LocalDate,
    registrationStartDate: LocalDate,
    registrationEndDate: LocalDate
)
pre:
    semesterCode <> null and semesterCode.size() > 0 and
    startDate <> null and endDate <> null and startDate < endDate and
    registrationStartDate <> null and registrationEndDate <> null and
    registrationStartDate < registrationEndDate and
    registrationStartDate <= startDate and registrationEndDate <= endDate
post:
    self.semesterCode = semesterCode and
    self.startDate = startDate and self.endDate = endDate and
    self.registrationStartDate = registrationStartDate and
    self.registrationEndDate = registrationEndDate and
    self.sections->isEmpty()
```

---

## 5. DEGREE PROGRAM - Collection Management

**Concept:** Academic degree with minimum credits and required courses.
**Attributes:** programName, minCredits, courses

### Invariants:

```ocl
-- Program name must not be empty
context DegreeProgram
inv programNameNotEmpty: self.programName <> null and self.programName.size() > 0

-- Minimum credits must be positive
context DegreeProgram
inv positiveMinCredits: self.minCredits > 0

-- Minimum credits should be reasonable (typically 120-180)
context DegreeProgram
inv reasonableMinCredits: self.minCredits >= 90 and self.minCredits <= 200

-- Courses list must not be null
context DegreeProgram
inv coursesNotNull: self.courses <> null

-- Courses should not contain duplicates (by course code)
context DegreeProgram
inv noDuplicateCourses:
    self.courses->forAll(c1, c2 | c1 <> c2 implies c1.courseCode <> c2.courseCode)

-- Total minimum credits from courses should match minCredits requirement
context DegreeProgram
inv creditRequirementMet:
    self.courses->collect(credits)->sum() >= self.minCredits
```

### Operations:

```ocl
-- List courses
context DegreeProgram::listCourses(): List(Course)
pre: true
post: result = self.courses->asSequence()

-- Add course (not explicitly in Java but implied contract)
context DegreeProgram
inv courseConsistency:
    self.courses->forAll(c | c <> null)
```

---

## 6. DEPARTMENT - Organization Unit

**Concept:** Administrative unit managing courses and instructors.
**Attributes:** deptCode, name, courses, instructors

### Invariants:

```ocl
-- Department code must not be empty and unique
context Department
inv deptCodeNotEmpty: self.deptCode <> null and self.deptCode.size() > 0

-- Department name must not be empty
context Department
inv nameNotEmpty: self.name <> null and self.name.size() > 0

-- Courses list must not be null
context Department
inv coursesNotNull: self.courses <> null

-- Instructors list must not be null
context Department
inv instructorsNotNull: self.instructors <> null

-- All courses belong only to this department
context Department
inv courseOwnership: self.courses->forAll(c | c.department = self)

-- All instructors in department (this constraint depends on Instructor model implementation)
context Department
inv instructorConsistency: self.instructors->forAll(i | i <> null)

-- No duplicate courses
context Department
inv noDuplicateCourses:
    self.courses->forAll(c1, c2 | c1 <> c2 implies c1.courseCode <> c2.courseCode)

-- No duplicate instructors
context Department
inv noDuplicateInstructors:
    self.instructors->forAll(i1, i2 | i1 <> i2 implies i1.id <> i2.id)
```

### Operations:

```ocl
-- List courses
context Department::listCourses(): List(Course)
pre: true
post: result = self.courses->asSequence()

-- List instructors
context Department::listInstructors(): List(Instructor)
pre: true
post: result = self.instructors->asSequence()

-- Constructor validation
context Department::Department(deptCode: String, name: String)
pre:
    deptCode <> null and deptCode.size() > 0 and
    name <> null and name.size() > 0
post:
    self.deptCode = deptCode and self.name = name and
    self.courses->isEmpty() and self.instructors->isEmpty()
```

---

## 7. COURSE - Academic Content with Prerequisites

**Concept:** Formal course with code, credits, department, and prerequisites.
**Attributes:** courseCode, title, description, credits, prerequisites, sections, department

### Invariants:

```ocl
-- Course code must not be empty
context Course
inv courseCodeNotEmpty: self.courseCode <> null and self.courseCode.size() > 0

-- Title must not be empty
context Course
inv titleNotEmpty: self.title <> null and self.title.size() > 0

-- Credits must be positive and reasonable (1-6 typically)
context Course
inv positiveCredits: self.credits > 0 and self.credits <= 6

-- Department must not be null
context Course
inv departmentNotNull: self.department <> null

-- Prerequisites should not contain circular dependencies
context Course
inv noCircularPrerequisites:
    self.prerequisites->excludes(self) and
    self.prerequisites->forAll(prereq |
        prereq.prerequisites->excludes(self)
    )

-- Course must belong to only one department
context Course
inv departmentOwnership: self.department.courses->includes(self)

-- Sections list must not be null
context Course
inv sectionsNotNull: self.sections <> null

-- All sections belong to this course
context Course
inv sectionBelongsToThisCourse:
    self.sections->forAll(s | s.course = self)

-- Prerequisites and their credits (informational)
context Course
inv prerequisitesValid: self.prerequisites->forAll(p | p.credits > 0)
```

### Derived Attributes:

```ocl
-- Total number of sections
context Course::getTotalSections(): Integer
post: result = self.sections->size()

-- Average course capacity across sections
context Course::getAverageCapacity(): Integer
post:
    if self.sections->isEmpty() then
        result = 0
    else
        result = self.sections->collect(capacity)->sum() / self.sections->size()
    endif
```

### Operations:

```ocl
-- Add prerequisite
context Course::addPrerequisite(course: Course): void
pre:
    course <> null and
    course <> self and
    not self.prerequisites->includes(course)
post: self.prerequisites->includes(course)

-- Constructor validation
context Course::Course(courseCode: String, title: String, credits: Int, department: Department)
pre:
    courseCode <> null and courseCode.size() > 0 and
    title <> null and title.size() > 0 and
    credits > 0 and credits <= 6 and
    department <> null
post:
    self.courseCode = courseCode and
    self.title = title and
    self.credits = credits and
    self.department = department and
    self.prerequisites->isEmpty() and
    self.sections->isEmpty()
```

---

## 8. COURSE SECTION - Course Offering in a Semester (COMPLEX)

**Concept:** Specific instance of a course with capacity, waitlist, and enrollment.
**Attributes:** sectionId, capacity, enrolledCount, waitlist, waitlistThreshold, course, semester, instructor, room, schedule, enrollments

### Invariants:

```ocl
-- Section ID must not be empty
context CourseSection
inv sectionIdNotEmpty: self.sectionId <> null and self.sectionId.size() > 0

-- Capacity must be positive
context CourseSection
inv positiveCapacity: self.capacity > 0

-- Enrolled count must be non-negative and not exceed capacity
context CourseSection
inv enrolledCountValid: self.enrolledCount >= 0 and self.enrolledCount <= self.capacity

-- No seats remaining should not be negative
context CourseSection
inv seatsRemainingValid: self.seatsRemaining() >= 0

-- Course must not be null
context CourseSection
inv courseNotNull: self.course <> null

-- Semester must not be null
context CourseSection
inv semesterNotNull: self.semester <> null

-- Section must belong to course
context CourseSection
inv sectionBelongsToCourse: self.course.sections->includes(self)

-- Section must belong to semester
context CourseSection
inv sectionBelongsToSemester: self.semester.sections->includes(self)

-- Waitlist must not be null
context CourseSection
inv waitlistNotNull: self.waitlist <> null

-- Waitlist threshold should be positive if set (typically 2x capacity)
context CourseSection
inv validWaitlistThreshold: self.waitlistThreshold >= self.capacity

-- Waitlist size should not exceed threshold
context CourseSection
inv waitlistSizeWithinThreshold:
    self.waitlist->size() <= self.waitlistThreshold

-- Enrollments list must not be null
context CourseSection
inv enrollmentsNotNull: self.enrollments <> null

-- Enrolled count must match actual enrolled enrollments
context CourseSection
inv enrolledCountConsistent:
    self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)->size() = self.enrolledCount

-- All enrolled enrollments must have valid students
context CourseSection
inv enrolledStudentsValid:
    self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)
        ->forAll(e | e.student <> null)

-- No duplicate enrollments for same student (only one active enrollment)
context CourseSection
inv noActiveDuplicateEnrollments:
    self.enrollments->select(e |
        e.status = EnrollmentStatus.ENROLLED or
        e.status = EnrollmentStatus.WAITLISTED
    )->forAll(e1, e2 |
        e1 <> e2 implies e1.student <> e2.student
    )

-- If section is full, all new enrollments should be waitlisted
context CourseSection
inv fullSectionHasWaitlist:
    if self.isFull() then
        self.enrollments->select(e | e.status = EnrollmentStatus.WAITLISTED)->size() >= 0
    else
        true
    endif

-- Room capacity (if assigned) must be at least section capacity
context CourseSection
inv roomCapacitySufficient:
    if self.room <> null then
        self.room.capacity >= self.capacity
    else
        true
    endif

-- If instructor is assigned, they must exist
context CourseSection
inv instructorValid:
    if self.instructor <> null then
        true
    else
        true  -- Instructor can be null (TBA)
    endif

-- If schedule is assigned, it must be valid
context CourseSection
inv scheduleValid:
    if self.schedule <> null then
        self.schedule.startTime < self.schedule.endTime
    else
        true  -- Schedule can be null (TBA)
    endif
```

### Queries and Operations:

```ocl
-- Calculate remaining seats
context CourseSection::seatsRemaining(): Integer
post: result = self.capacity - self.enrolledCount

-- Check if section is full
context CourseSection::isFull(): Boolean
post: result = self.seatsRemaining() <= 0

-- Check if section is active (within semester dates)
context CourseSection::isActive(): Boolean
pre: true
post: result =
    let today = LocalDate.now() in
    not today.isBefore(self.semester.startDate) and
    not today.isAfter(self.semester.endDate)

-- Update waitlist
context CourseSection::updateWaitlist(student: Student): void
pre: student <> null and not self.waitlist->includes(student)
post:
    if self.waitlist->size() < self.waitlistThreshold then
        self.waitlist->includes(student)
    else
        self.waitlist->excludes(student)
    endif

-- Constructor validation
context CourseSection::CourseSection(sectionId: String, capacity: Integer, course: Course, semester: Semester)
pre:
    sectionId <> null and sectionId.size() > 0 and
    capacity > 0 and
    course <> null and
    semester <> null
post:
    self.sectionId = sectionId and
    self.capacity = capacity and
    self.course = course and
    self.semester = semester and
    self.enrolledCount = 0 and
    self.waitlist->isEmpty() and
    self.enrollments->isEmpty()
```

---

## 9. ENROLLMENT - Student Registration Record (COMPLEX)

**Concept:** Association between student and course section with status and grade.
**Attributes:** enrollmentDateTime, status, grade, student, section

### Invariants:

```ocl
-- Enrollment date/time must not be null
context Enrollment
inv enrollmentDateTimeNotNull: self.enrollmentDateTime <> null

-- Status must not be null
context Enrollment
inv statusNotNull: self.status <> null

-- Student must not be null
context Enrollment
inv studentNotNull: self.student <> null

-- Section must not be null
context Enrollment
inv sectionNotNull: self.section <> null

-- Enrollment must be in the section's enrollments
context Enrollment
inv belongsToSection: self.section.enrollments->includes(self)

-- Enrollment must be in the student's enrollments
context Enrollment
inv belongsToStudent: self.student.enrollments->includes(self)

-- Only ENROLLED status students have grades
context Enrollment
inv gradeConsistency:
    if self.status = EnrollmentStatus.ENROLLED then
        true  -- Can have or not have grade depending on semester progress
    else
        true  -- Non-enrolled students may not have grades
    endif

-- Valid grades (if present): A, A-, B+, B, B-, C+, C, C-, D+, D, F, or null
context Enrollment
inv validGrade:
    if self.grade <> null then
        self.grade = 'A' or self.grade = 'A-' or self.grade = 'B+' or
        self.grade = 'B' or self.grade = 'B-' or self.grade = 'C+' or
        self.grade = 'C' or self.grade = 'C-' or self.grade = 'D+' or
        self.grade = 'D' or self.grade = 'F'
    else
        true
    endif

-- Enrollment time should be within semester registration window
context Enrollment
inv enrollmentTimeValid:
    let semester = self.section.semester in
    self.enrollmentDateTime.toLocalDate() >= semester.registrationStartDate and
    self.enrollmentDateTime.toLocalDate() <= semester.registrationEndDate
```

### Operations:

```ocl
-- Constructor validation
context Enrollment::Enrollment(
    enrollmentDateTime: LocalDateTime,
    status: EnrollmentStatus,
    student: Student,
    section: CourseSection
)
pre:
    enrollmentDateTime <> null and
    status <> null and
    student <> null and
    section <> null
post:
    self.enrollmentDateTime = enrollmentDateTime and
    self.status = status and
    self.student = student and
    self.section = section and
    self.grade = null

-- Set grade (for instructor to call)
context Enrollment::setGrade(grade: String): void
pre:
    (grade = 'A' or grade = 'A-' or grade = 'B+' or grade = 'B' or grade = 'B-' or
     grade = 'C+' or grade = 'C' or grade = 'C-' or grade = 'D+' or grade = 'D' or
     grade = 'F' or grade = null) and
    self.status <> EnrollmentStatus.WAITLISTED and
    self.status <> EnrollmentStatus.DROPPED
post: self.grade = grade
```

---

## 10. STUDENT - Student Registration and Enrollment (VERY COMPLEX)

**Concept:** Student with credit limits, degree program, and enrollments.
**Attributes:** creditLimit, degreeProgram, enrollments (inherited: id, name, email, username, password, loggedIn)

### Invariants:

```ocl
-- Credit limit must be positive and reasonable (12-18 typically)
context Student
inv creditLimitValid: self.creditLimit > 0 and self.creditLimit <= 24

-- Enrollments list must not be null
context Student
inv enrollmentsNotNull: self.enrollments <> null

-- All enrollments belong to this student
context Student
inv enrollmentConsistency:
    self.enrollments->forAll(e | e.student = self)

-- Degree program (if present) must be valid
context Student
inv degreeProgramValid:
    if self.degreeProgram <> null then
        self.degreeProgram.courses->size() >= 0
    else
        true  -- Student might not have degree program assigned
    endif

-- Student cannot be enrolled in same section twice (only one active enrollment per section)
context Student
inv noActiveDuplicateEnrollments:
    self.enrollments->select(e |
        e.status = EnrollmentStatus.ENROLLED or
        e.status = EnrollmentStatus.WAITLISTED
    )->forAll(e1, e2 |
        e1 <> e2 implies e1.section <> e2.section
    )

-- Total credits in current semester should not exceed credit limit
context Student
inv creditLimitNotExceeded:
    let currSemester = Semester.getCurrent() in
    if currSemester <> null then
        let semesterCredits = self.enrollments
            ->select(e |
                e.status = EnrollmentStatus.ENROLLED and
                e.section.semester = currSemester
            )->collect(e | e.section.course.credits)->sum() in
        semesterCredits <= self.creditLimit
    else
        true
    endif

-- Prerequisites must be satisfied for all enrolled courses
context Student
inv prerequisitesSatisfied:
    self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)
        ->forAll(e |
            let course = e.section.course in
            course.prerequisites->forAll(prereq |
                self.enrollments->exists(prevE |
                    prevE.section.course = prereq and
                    prevE.grade <> null and prevE.grade <> 'F'
                )
            )
        )

-- No schedule conflicts for enrolled courses in same semester
context Student
inv noScheduleConflicts:
    self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)
        ->groupBy(e | e.section.semester)->forAll(semester, enrollmentsInSem |
            enrollmentsInSem->forAll(e1, e2 |
                e1 <> e2 implies
                not (e1.section.schedule <> null and e2.section.schedule <> null and
                     e1.section.schedule.overlapsRoom(e2.section.schedule))
            )
        )

-- Cannot enroll in course already completed (if tracking history)
context Student
inv noDuplicateCourseCompletion:
    self.enrollments->select(e |
        e.status = EnrollmentStatus.ENROLLED and e.grade <> null and e.grade <> 'F'
    )->forAll(e1, e2 |
        e1 <> e2 implies e1.section.course <> e2.section.course
    )

-- Enrolled students must have valid sections (not null)
context Student
inv enrolledSectionsValid:
    self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)
        ->forAll(e | e.section <> null and e.section.course <> null)
```

### Derived Attributes:

```ocl
-- Total credits for a semester
context Student::getTotalCredits(semester: Semester): Integer
post: result =
    self.enrollments->select(e |
        e.status = EnrollmentStatus.ENROLLED and
        e.section.semester = semester
    )->collect(e | e.section.course.credits)->sum()

-- List enrollments in a semester
context Student::listEnrollments(semester: Semester): Sequence(Enrollment)
post: result =
    self.enrollments->select(e | e.section.semester = semester)->asSequence()

-- Check if has schedule conflict
context Student::hasScheduleConflict(section: CourseSection): Boolean
post: result =
    not self.getConflictingSections(section)->isEmpty()

-- Get conflicting sections
context Student::getConflictingSections(section: CourseSection): Set(CourseSection)
post: result =
    self.enrollments->select(e |
        e.status = EnrollmentStatus.ENROLLED and
        e.section.semester = section.semester
    )->select(e |
        let currentSchedule = e.section.schedule,
            incomingSchedule = section.schedule in
        currentSchedule <> null and incomingSchedule <> null and
        currentSchedule.overlapsRoom(incomingSchedule)
    )->collect(e | e.section)
```

### Operations:

```ocl
-- Enroll in course section
context Student::enroll(section: CourseSection, semester: Semester): void
pre:
    section <> null and
    semester <> null and
    semester.isWithinRegistrationDate(LocalDate.now()) and
    section.semester = semester and
    not self.hasActiveEnrollmentInSection(section) and
    (self.degreeProgram = null or self.degreeProgram.courses->includes(section.course)) and
    self.getTotalCredits(semester) + section.course.credits <= self.creditLimit and
    not self.hasScheduleConflict(section)
post:
    if section.isFull() then
        self.enrollments->exists(e |
            e.section = section and e.status = EnrollmentStatus.WAITLISTED
        )
    else
        self.enrollments->exists(e |
            e.section = section and e.status = EnrollmentStatus.ENROLLED
        )
    endif

-- Drop course
context Student::drop(section: CourseSection, semester: Semester, today: LocalDate): void
pre:
    section <> null and
    semester <> null and
    today <> null and
    semester.isWithinRegistrationDate(today) and
    self.enrollments->exists(e |
        e.section = section and
        (e.status = EnrollmentStatus.ENROLLED or
         e.status = EnrollmentStatus.WAITLISTED)
    )
post:
    let enrollment = self.enrollments->select(e | e.section = section)->first() in
    enrollment.status = EnrollmentStatus.DROPPED

-- Browser courses
context Student::browseCoursesBySemester(semester: Semester): Sequence(Course)
pre: semester <> null
post: result =
    semester.sections->collect(s | s.course)->asSet()->asSequence()

-- Search courses
context Student::searchCourses(semester: Semester, keyword: String): Sequence(Course)
pre: semester <> null and keyword <> null
post: result =
    self.browseCoursesBySemester(semester)->select(c |
        c.courseCode.toLower().contains(keyword.toLower()) or
        c.title.toLower().contains(keyword.toLower())
    )->asSequence()

-- Constructor validation
context Student::Student(
    id: String, name: String, email: String, username: String, password: String,
    creditLimit: Integer, degreeProgram: DegreeProgram
)
pre:
    id <> null and id.size() > 0 and
    name <> null and name.size() > 0 and
    email <> null and email.indexOf('@') > 0 and
    username <> null and username.size() >= 3 and
    password <> null and password.size() >= 6 and
    creditLimit > 0 and creditLimit <= 24
post:
    self.id = id and self.name = name and self.email = email and
    self.username = username and self.password = password and
    self.creditLimit = creditLimit and
    self.degreeProgram = degreeProgram and
    self.enrollments->isEmpty() and
    self.loggedIn = false
```

---

## 11. INSTRUCTOR - Course Teacher (COMPLEX)

**Concept:** Instructor who teaches course sections and grades students.
**Attributes:** (inherited from Person: id, name, email, username, password, loggedIn)

### Invariants:

```ocl
-- Instructor must belong to at least one department (implied by sections)
context Instructor
inv hasValidDepartment: true  -- Depends on implementation

-- All assigned sections are unique
context Instructor
inv uniqueAssignedSections:
    let allSections = Semester.allInstances()->collect(s |
        s.sections->select(sec | sec.instructor = self)
    )->flatten() in
    allSections->forAll(s1, s2 | s1 <> s2 implies s1.sectionId <> s2.sectionId)

-- Instructor cannot have schedule conflicts in same semester
context Instructor
inv noScheduleConflicts:
    let semesters = Semester.allInstances() in
    semesters->forAll(sem |
        let instructorSections = sem.sections->select(sec | sec.instructor = self) in
        instructorSections->forAll(s1, s2 |
            s1 <> s2 implies
            (s1.schedule = null or s2.schedule = null or
             not s1.schedule.overlapsRoom(s2.schedule))
        )
    )

-- All grades given must be for assigned sections
context Instructor
inv validGradeAssignments:
    Enrollment.allInstances()->select(e | e.grade <> null)
        ->forAll(e |
            e.section.instructor = self
        )
```

### Operations:

```ocl
-- View assigned sections for a semester
context Instructor::viewAssignedSections(semester: Semester): Sequence(CourseSection)
pre: semester <> null
post: result =
    semester.sections->select(s | s.instructor = self)->asSequence()

-- Add student grade
context Instructor::addStudentGrade(section: CourseSection, student: Student, grade: String): void
pre:
    section <> null and
    student <> null and
    grade <> null and
    section.instructor = self and
    section.enrollments->exists(e | e.student = student) and
    (grade = 'A' or grade = 'A-' or grade = 'B+' or grade = 'B' or grade = 'B-' or
     grade = 'C+' or grade = 'C' or grade = 'C-' or grade = 'D+' or grade = 'D' or
     grade = 'F')
post:
    section.enrollments->select(e | e.student = student)->forAll(e | e.grade = grade)

-- View enrolled students for their section
context Instructor::viewEnrolledStudents(section: CourseSection): Sequence(Enrollment)
pre:
    section <> null and
    section.instructor = self
post:
    result = section.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)->asSequence()

-- Constructor validation (from Person)
context Instructor::Instructor(id: String, name: String, email: String, username: String, password: String)
pre:
    id <> null and id.size() > 0 and
    name <> null and name.size() > 0 and
    email <> null and email.indexOf('@') > 0 and
    username <> null and username.size() >= 3 and
    password <> null and password.size() >= 6
post:
    self.id = id and self.name = name and self.email = email and
    self.username = username and self.password = password and
    self.loggedIn = false
```

---

## 12. ADMIN - System Administrator (MOST COMPLEX)

**Concept:** Administrative user managing courses, sections, and instructors.
**Attributes:** (inherited from Person: id, name, email, username, password, loggedIn)

### Invariants:

```ocl
-- Admin is a person with all validation constraints from Person
context Admin
inv isValidPerson:
    self.id <> null and self.id.size() > 0 and
    self.name <> null and self.name.size() > 0 and
    self.email <> null and self.email.indexOf('@') > 0 and
    self.username <> null and self.username.size() >= 3 and
    self.password <> null and self.password.size() >= 6

-- All administrative actions must maintain system integrity
context Admin
inv systemIntegrity:
    Course.allInstances()->forAll(c | c.department <> null) and
    CourseSection.allInstances()->forAll(s | s.course <> null and s.semester <> null)
```

### Operations (CRITICAL):

```ocl
-- Add course to department
context Admin::addCourse(course: Course): void
pre:
    course <> null and
    course.courseCode <> null and course.courseCode.size() > 0 and
    course.title <> null and course.title.size() > 0 and
    course.credits > 0 and course.credits <= 6 and
    course.department <> null and
    not course.department.courses->includes(course)
post:
    course.department.courses->includes(course) and
    Course.allInstances()->includes(course)

-- Update course details
context Admin::updateCourse(course: Course): void
pre:
    course <> null and
    course.courseCode <> null and course.courseCode.size() > 0 and
    course.title <> null and course.title.size() > 0 and
    course.credits > 0 and course.credits <= 6 and
    course.department <> null and
    course.department.courses->includes(course)
post:
    Course.allInstances()->includes(course)

-- Create new section
context Admin::createSection(course: Course, semester: Semester, section: CourseSection): void
pre:
    course <> null and
    semester <> null and
    section <> null and
    section.course = course and
    section.semester = semester and
    section.sectionId <> null and section.sectionId.size() > 0 and
    section.capacity > 0 and
    not course.sections->includes(section) and
    not semester.sections->includes(section)
post:
    course.sections->includes(section) and
    semester.sections->includes(section)

-- Create new course
context Admin::createCourse(
    courseCode: String,
    title: String,
    description: String,
    credits: Integer,
    department: Department
): Course
pre:
    courseCode <> null and courseCode.size() > 0 and
    title <> null and title.size() > 0 and
    credits > 0 and credits <= 6 and
    department <> null and
    not department.courses->exists(c | c.courseCode = courseCode)
post:
    result.courseCode = courseCode and
    result.title = title and
    result.description = description and
    result.credits = credits and
    result.department = department and
    department.courses->includes(result) and
    result.prerequisites->isEmpty() and
    result.sections->isEmpty()

-- Assign instructor to section
context Admin::assignInstructor(section: CourseSection, instructor: Instructor): void
pre:
    section <> null and
    instructor <> null and
    not instructor.hasScheduleConflict(section)
post:
    section.instructor = instructor and
    -- Verify no schedule conflicts created
    semester.sections->select(s | s.instructor = instructor and s <> section)
        ->forAll(otherSection |
            otherSection.schedule = null or section.schedule = null or
            not otherSection.schedule.overlapsRoom(section.schedule)
        )

-- Constructor validation
context Admin::Admin(id: String, name: String, email: String, username: String, password: String)
pre:
    id <> null and id.size() > 0 and
    name <> null and name.size() > 0 and
    email <> null and email.indexOf('@') > 0 and
    username <> null and username.size() >= 3 and
    password <> null and password.size() >= 6
post:
    self.id = id and self.name = name and self.email = email and
    self.username = username and self.password = password and
    self.loggedIn = false
```

---

## 13. SYSTEM-LEVEL CONSTRAINTS (Interactions between multiple concepts)

### Cross-Entity Constraints:

```ocl
-- All semesters must have unique codes
context Semester
inv uniqueSemesterCodes:
    Semester.allInstances()->forAll(s1, s2 |
        s1 <> s2 implies s1.semesterCode <> s2.semesterCode
    )

-- All courses must have unique course codes
context Course
inv uniqueCourseCodes:
    Course.allInstances()->forAll(c1, c2 |
        c1 <> c2 implies c1.courseCode <> c2.courseCode
    )

-- All users must have unique IDs
context Person
inv uniqueUserIds:
    Person.allInstances()->forAll(p1, p2 |
        p1 <> p2 implies p1.id <> p2.id
    )

-- All users must have unique usernames
context Person
inv uniqueUsernames:
    Person.allInstances()->forAll(p1, p2 |
        p1 <> p2 implies p1.username <> p2.username
    )

-- Total enrollment (enrolled + waitlisted) should not exceed reasonable bounds
context CourseSection
inv enrollmentBounds:
    let totalEnrollments = self.enrollments->size() in
    let enrolledCount = self.enrollments->select(e | e.status = EnrollmentStatus.ENROLLED)->size() in
    enrolledCount <= self.capacity and
    totalEnrollments <= self.capacity + self.waitlist->size()

-- Instructor can only grade enrollments in their sections
context Instructor
inv gradingAuthority:
    Enrollment.allInstances()->select(e | e.grade <> null)
        ->forAll(e | e.section.instructor = self)

-- Student cannot enroll outside registration window
context Student
inv enrollmentWindowRespected:
    self.enrollments->forAll(e |
        let sem = e.section.semester in
        e.enrollmentDateTime.toLocalDate() >= sem.registrationStartDate and
        e.enrollmentDateTime.toLocalDate() <= sem.registrationEndDate
    )

-- Prerequisite chain must be satisfiable (no circular dependencies)
context Course
inv prerequisiteChainResolvable:
    self.prerequisites->forAll(prereq |
        not prereq.prerequisites->closure(p | p.prerequisites)->includes(self)
    )

-- Room cannot be double-booked at same time
context Room
inv noDoubleBooking:
    CourseSection.allInstances()->select(s1 | s1.room = self)
        ->forAll(s1, s2 |
            s1 <> s2 implies (
                s1.schedule = null or s2.schedule = null or
                s1.semester <> s2.semester or
                not s1.schedule.overlapsRoom(s2.schedule)
            )
        )
```

---

## Summary: Complexity Levels

| Concept       | Complexity  | Key Constraints                          |
| ------------- | ----------- | ---------------------------------------- |
| Room          | Very Simple | Positive capacity, non-empty fields      |
| Schedule      | Simple      | Time validity, overlap detection         |
| Person        | Simple      | Email format, password security          |
| Semester      | Medium      | Date ranges, registration windows        |
| DegreeProgram | Medium      | Credit requirements, no duplicates       |
| Department    | Medium      | Course/instructor consistency            |
| Course        | Medium-High | Prerequisites, no circular deps          |
| CourseSection | High        | Capacity, enrollment, waitlist logic     |
| Enrollment    | High        | Status-grade consistency, time validity  |
| Student       | Very High   | Credits limits, prerequisites, conflicts |
| Instructor    | High        | Section assignment, schedule conflicts   |
| Admin         | Very High   | System integrity, cascading operations   |
| System-Level  | Critical    | Cross-entity constraints, uniqueness     |

---

## Notes for Implementation:

1. **OCL Tools**: Use tools like USE (UML Specification Environment) or Papyrus to validate these constraints
2. **Code Integration**: These OCL constraints should be translated to code-level validation (which your tests already do)
3. **Temporal Aspects**: Some constraints involve time (dates, registration windows) - ensure system clock is tested
4. **Reflexivity**: Several constraints assume access to `allInstances()` - this depends on your metamodeling approach
5. **Performance**: Constraints with `forAll` and `closure` operations may be expensive; consider caching
6. **Testing**: Each constraint should have corresponding unit tests

---

**Next Steps**: Would you like me to help you:

1. Create a visual UML diagram with these constraints?
2. Implement these as JUnit test cases?
3. Create an OCL validation file for a specific tool?
4. Explain or expand any specific constraint?
