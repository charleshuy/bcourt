package org.swp391grp3.bcourt.utilTests;

import org.junit.jupiter.api.Test;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void isValidEmail_whenValidEmail_shouldReturnTrue() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
    }

    @Test
    void isValidEmail_whenInvalidEmail_shouldReturnFalse() {
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
    }

    @Test
    void isValidPhoneNumber_whenValidPhoneNumber_shouldReturnTrue() {
        assertTrue(ValidationUtil.isValidPhoneNumber("1234567890"));
    }

    @Test
    void isValidPhoneNumber_whenInvalidPhoneNumber_shouldReturnFalse() {
        assertFalse(ValidationUtil.isValidPhoneNumber("12345"));
        assertFalse(ValidationUtil.isValidPhoneNumber("12345678901"));
        assertFalse(ValidationUtil.isValidPhoneNumber("abcdefg123"));
    }
}