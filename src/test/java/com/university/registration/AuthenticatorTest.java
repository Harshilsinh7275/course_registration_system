package com.university.registration;

import com.university.registration.model.Authenticator;
import com.university.registration.model.Admin;
import com.university.registration.model.DegreeProgram;
import com.university.registration.model.Person;
import com.university.registration.model.SessionState;
import com.university.registration.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticatorTest {

    @Test
    void validCredentialsLoginAndLogout() {
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, new DegreeProgram("BSc", 120));
        Authenticator authenticator = new Authenticator(List.of(student));

        Person user = authenticator.login("alice", "pass");
        assertNotNull(user);
        assertTrue(user.isLoggedIn());
        assertTrue(user.getSessionState() == SessionState.LOGGED_IN);

        authenticator.logout(user);
        assertFalse(user.isLoggedIn());
        assertTrue(user.getSessionState() == SessionState.LOGGED_OUT);
    }

    @Test
    void invalidCredentialsReturnNull() {
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, new DegreeProgram("BSc", 120));
        Authenticator authenticator = new Authenticator(List.of(student));

        Person user = authenticator.login("alice", "wrong-password");
        assertNull(user);
        assertTrue(student.getSessionState() == SessionState.LOGGED_OUT);
    }

    @Test
    void duplicateAccountRegistrationIsBlocked() {
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, new DegreeProgram("BSc", 120));
        Student duplicate = new Student("S1", "Alice2", "a2@u.com", "alice2", "pass", 12, new DegreeProgram("BSc", 120));
        Authenticator authenticator = new Authenticator();
        authenticator.registerUser(student);

        assertThrows(IllegalStateException.class, () -> authenticator.registerUser(duplicate));
    }

    @Test
    void roleAuthorizationMatchesUserType() {
        Student student = new Student("S1", "Alice", "a@u.com", "alice", "pass", 12, new DegreeProgram("BSc", 120));
        Admin admin = new Admin("A1", "Admin", "admin@u.com", "admin", "pass");
        Authenticator authenticator = new Authenticator();

        assertTrue(authenticator.isRoleAuthorized(student, "STUDENT"));
        assertTrue(authenticator.isRoleAuthorized(admin, "ADMIN"));
        assertFalse(authenticator.isRoleAuthorized(student, "ADMIN"));
    }
}
