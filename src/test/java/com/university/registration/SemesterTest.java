package com.university.registration;

import com.university.registration.model.Semester;
import com.university.registration.model.SemesterState;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemesterTest {

    @Test
    void registrationDateValidation() {
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );

        assertTrue(semester.isWithinRegistrationDate(LocalDate.of(2026, 7, 15)));
        assertFalse(semester.isWithinRegistrationDate(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void semesterStateTransitionsFollowAcademicTimeline() {
        Semester semester = new Semester(
                "FALL-2026",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 31)
        );

        assertTrue(semester.refreshState(LocalDate.of(2026, 6, 30)) == SemesterState.PLANNED);
        assertTrue(semester.refreshState(LocalDate.of(2026, 7, 15)) == SemesterState.REGISTRATION_OPEN);
        assertTrue(semester.refreshState(LocalDate.of(2026, 8, 31)) == SemesterState.REGISTRATION_OPEN);
        assertTrue(semester.refreshState(LocalDate.of(2026, 9, 1)) == SemesterState.IN_PROGRESS);
        assertTrue(semester.refreshState(LocalDate.of(2026, 12, 21)) == SemesterState.COMPLETED);
    }
}
