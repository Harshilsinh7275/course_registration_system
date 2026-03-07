package com.university.registration;

import com.university.registration.model.Schedule;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleTest {

    @Test
    void overlapDetection() {
        Schedule s1 = new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Schedule s2 = new Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));
        Schedule s3 = new Schedule(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));

        assertTrue(s1.overlapsRoom(s2));
        assertFalse(s1.overlapsRoom(s3));
    }
}

