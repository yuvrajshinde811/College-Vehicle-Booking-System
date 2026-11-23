package com.yuvraj.test;

import com.yuvraj.dao.UserDAO;
import com.yuvraj.database.DBConnection;
import com.yuvraj.database.PasswordUtil;
import com.yuvraj.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * TestUserRegistration verifies JDBC connection, user registration,
 * duplicate email detection, password hashing, and authentication.
 * Can be run via JUnit 5 or as a standalone Java Application in Eclipse.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserRegistration {

    private static UserDAO userDAO;
    private static final String TEST_EMAIL = "junit.student@college.edu";
    private static final String TEST_PASS = "Secret@123";

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAO();
        // Clean up test user if previously left in DB
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE email = ?")) {
            ps.setString(1, TEST_EMAIL);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cleanup notice: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testDatabaseConnection() {
        System.out.println("TEST 1: Verifying Database Connection...");
        try (Connection conn = DBConnection.getConnection()) {
            Assertions.assertNotNull(conn, "Connection should not be null");
            Assertions.assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("  -> SUCCESS: Connected to " + conn.getCatalog());
        } catch (Exception e) {
            Assertions.fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testPasswordHashing() {
        System.out.println("TEST 2: Verifying Password Hashing...");
        String hash = PasswordUtil.hashPassword(TEST_PASS);
        Assertions.assertNotNull(hash, "Password hash must not be null");
        Assertions.assertTrue(hash.startsWith("210000:"), "Hash format should include iteration count");

        boolean matches = PasswordUtil.verifyPassword(TEST_PASS, hash);
        boolean wrongMatches = PasswordUtil.verifyPassword("WrongPassword", hash);

        Assertions.assertTrue(matches, "Valid password should verify successfully");
        Assertions.assertFalse(wrongMatches, "Invalid password should fail verification");
        System.out.println("  -> SUCCESS: Password hashing and constant-time verification working.");
    }

    @Test
    @Order(3)
    public void testUserRegistration() {
        System.out.println("TEST 3: Verifying User Registration via UserDAO...");
        String passwordHash = PasswordUtil.hashPassword(TEST_PASS);
        User user = new User("JUnit Test Student", TEST_EMAIL, passwordHash, "STUDENT", "MCA", "9876500001");

        boolean registered = userDAO.registerUser(user);
        Assertions.assertTrue(registered, "User registration should return true");
        Assertions.assertTrue(user.getUserId() > 0, "Generated user ID should be positive");
        System.out.println("  -> SUCCESS: Registered user ID = " + user.getUserId());
    }

    @Test
    @Order(4)
    public void testDuplicateEmailPrevention() {
        System.out.println("TEST 4: Verifying Duplicate Email Prevention...");
        String passwordHash = PasswordUtil.hashPassword("AnotherPassword");
        User duplicateUser = new User("Duplicate User", TEST_EMAIL, passwordHash, "STUDENT", "MCA", "9876500002");

        boolean duplicateResult = userDAO.registerUser(duplicateUser);
        Assertions.assertFalse(duplicateResult, "Duplicate email registration must fail");
        Assertions.assertTrue(userDAO.emailExists(TEST_EMAIL), "emailExists should return true");
        System.out.println("  -> SUCCESS: Duplicate email correctly rejected.");
    }

    @Test
    @Order(5)
    public void testUserAuthentication() {
        System.out.println("TEST 5: Verifying User Authentication (Login)...");
        User authenticated = userDAO.authenticate(TEST_EMAIL, TEST_PASS);
        Assertions.assertNotNull(authenticated, "User should authenticate with correct credentials");
        Assertions.assertEquals("STUDENT", authenticated.getRole());
        Assertions.assertEquals("JUnit Test Student", authenticated.getFullName());

        User badAuth = userDAO.authenticate(TEST_EMAIL, "WrongPassword123");
        Assertions.assertNull(badAuth, "Authentication must fail with incorrect password");
        System.out.println("  -> SUCCESS: Authentication logic verified.");
    }

    /**
     * Standalone main method so the student can run this test directly in Eclipse.
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Running Standalone TestUserRegistration Runner ");
        System.out.println("==================================================");
        setUp();
        TestUserRegistration test = new TestUserRegistration();
        try {
            test.testDatabaseConnection();
            test.testPasswordHashing();
            test.testUserRegistration();
            test.testDuplicateEmailPrevention();
            test.testUserAuthentication();
            System.out.println("\nALL TESTS PASSED SUCCESSFULLY!");
        } catch (Throwable t) {
            System.err.println("\nTEST FAILED: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
