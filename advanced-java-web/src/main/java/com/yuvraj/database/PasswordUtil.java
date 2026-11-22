
package com.yuvraj.database;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    // Generate a secure password hash
    public static String hashPassword(String password) {

        try {
            SecureRandom random = new SecureRandom();

            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            String encodedSalt =
                Base64.getEncoder().encodeToString(salt);

            String encodedHash =
                Base64.getEncoder().encodeToString(hash);

            return ITERATIONS + ":" + encodedSalt + ":" + encodedHash;

        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    // Verify a password during login
    public static boolean verifyPassword(
            String password,
            String storedHash) {

        try {
            String[] parts = storedHash.split(":");

            int iterations = Integer.parseInt(parts[0]);

            byte[] salt =
                Base64.getDecoder().decode(parts[1]);

            byte[] originalHash =
                Base64.getDecoder().decode(parts[2]);

            KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                originalHash.length * 8
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] newHash = factory.generateSecret(spec).getEncoded();

            return java.security.MessageDigest.isEqual(
                originalHash, newHash
            );

        } catch (Exception e) {
            return false;
        }
    }
}
