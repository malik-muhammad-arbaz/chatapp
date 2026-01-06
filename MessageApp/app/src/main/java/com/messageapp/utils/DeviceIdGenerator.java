package com.messageapp.utils;

import java.security.SecureRandom;

public class DeviceIdGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a unique 8-character alphanumeric device ID
     * Format: XXXX-XXXX (e.g., A1B2-C3D4)
     */
    public static String generateDeviceId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH + 1);
        
        for (int i = 0; i < ID_LENGTH; i++) {
            if (i == 4) {
                sb.append("-");
            }
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        
        return sb.toString();
    }

    /**
     * Validates if the given string is a valid device ID format
     */
    public static boolean isValidDeviceId(String deviceId) {
        if (deviceId == null || deviceId.length() != 9) {
            return false;
        }
        
        // Check format: XXXX-XXXX
        if (deviceId.charAt(4) != '-') {
            return false;
        }
        
        String withoutDash = deviceId.replace("-", "");
        for (char c : withoutDash.toCharArray()) {
            if (CHARACTERS.indexOf(c) == -1) {
                return false;
            }
        }
        
        return true;
    }
}
