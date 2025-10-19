package com.misw.medisupply.core.utils

import org.junit.Test
import org.junit.Assert.*

class ValidationUtilsTest {

    // NIT Validation Tests
    @Test
    fun `isValidNit returns true for valid NIT format`() {
        assertTrue(ValidationUtils.isValidNit("123456789-0"))
        assertTrue(ValidationUtils.isValidNit("900123456-7"))
    }

    @Test
    fun `isValidNit returns false for invalid NIT format`() {
        assertFalse(ValidationUtils.isValidNit("12345678-0"))  // Too short
        assertFalse(ValidationUtils.isValidNit("1234567890-0")) // Too long
        assertFalse(ValidationUtils.isValidNit("123456789"))    // Missing dash
        assertFalse(ValidationUtils.isValidNit("123456789-"))   // Missing digit
        assertFalse(ValidationUtils.isValidNit("12345678A-0"))  // Letter instead of number
    }

    // Phone Validation Tests
    @Test
    fun `isValidColombianPhone returns true for valid phone numbers`() {
        assertTrue(ValidationUtils.isValidColombianPhone("3001234567"))
        assertTrue(ValidationUtils.isValidColombianPhone("+573001234567"))
        assertTrue(ValidationUtils.isValidColombianPhone("3101234567"))
        assertTrue(ValidationUtils.isValidColombianPhone("3201234567"))
    }

    @Test
    fun `isValidColombianPhone handles spaces and dashes`() {
        assertTrue(ValidationUtils.isValidColombianPhone("300 123 4567"))
        assertTrue(ValidationUtils.isValidColombianPhone("300-123-4567"))
        assertTrue(ValidationUtils.isValidColombianPhone("(300) 123-4567"))
    }

    @Test
    fun `isValidColombianPhone returns false for invalid phone numbers`() {
        assertFalse(ValidationUtils.isValidColombianPhone("12345"))       // Too short
        assertFalse(ValidationUtils.isValidColombianPhone("12345678901")) // Too long
        assertFalse(ValidationUtils.isValidColombianPhone("abcdefghij"))  // Letters
        assertFalse(ValidationUtils.isValidColombianPhone(""))             // Empty
    }

    // Empty Validation Tests
    @Test
    fun `isNotEmpty returns true for non-empty strings`() {
        assertTrue(ValidationUtils.isNotEmpty("test"))
        assertTrue(ValidationUtils.isNotEmpty("   text   "))
        assertTrue(ValidationUtils.isNotEmpty("123"))
    }

    @Test
    fun `isNotEmpty returns false for empty or null strings`() {
        assertFalse(ValidationUtils.isNotEmpty(null))
        assertFalse(ValidationUtils.isNotEmpty(""))
        assertFalse(ValidationUtils.isNotEmpty("   "))
        assertFalse(ValidationUtils.isNotEmpty("\t"))
    }

    // Length Validation Tests
    @Test
    fun `hasMinLength returns true when string meets minimum length`() {
        assertTrue(ValidationUtils.hasMinLength("test", 4))
        assertTrue(ValidationUtils.hasMinLength("testing", 4))
        assertTrue(ValidationUtils.hasMinLength("test", 1))
    }

    @Test
    fun `hasMinLength returns false when string is too short`() {
        assertFalse(ValidationUtils.hasMinLength("tes", 4))
        assertFalse(ValidationUtils.hasMinLength("", 1))
    }

    @Test
    fun `hasMaxLength returns true when string is within maximum length`() {
        assertTrue(ValidationUtils.hasMaxLength("test", 4))
        assertTrue(ValidationUtils.hasMaxLength("tes", 4))
        assertTrue(ValidationUtils.hasMaxLength("", 4))
    }

    @Test
    fun `hasMaxLength returns false when string exceeds maximum length`() {
        assertFalse(ValidationUtils.hasMaxLength("testing", 4))
        assertFalse(ValidationUtils.hasMaxLength("tests", 4))
    }

    // Number Validation Tests
    @Test
    fun `isPositiveNumber returns true for positive numbers`() {
        assertTrue(ValidationUtils.isPositiveNumber(1.0))
        assertTrue(ValidationUtils.isPositiveNumber(100.5))
        assertTrue(ValidationUtils.isPositiveNumber(0.1))
    }

    @Test
    fun `isPositiveNumber returns false for zero and negative numbers`() {
        assertFalse(ValidationUtils.isPositiveNumber(0.0))
        assertFalse(ValidationUtils.isPositiveNumber(-1.0))
        assertFalse(ValidationUtils.isPositiveNumber(-100.5))
    }

    // Range Validation Tests
    @Test
    fun `isInRange returns true when value is within range`() {
        assertTrue(ValidationUtils.isInRange(5.0, 1.0, 10.0))
        assertTrue(ValidationUtils.isInRange(1.0, 1.0, 10.0))
        assertTrue(ValidationUtils.isInRange(10.0, 1.0, 10.0))
    }

    @Test
    fun `isInRange returns false when value is outside range`() {
        assertFalse(ValidationUtils.isInRange(0.0, 1.0, 10.0))
        assertFalse(ValidationUtils.isInRange(11.0, 1.0, 10.0))
        assertFalse(ValidationUtils.isInRange(-5.0, 1.0, 10.0))
    }

    // Credit Limit Validation Tests
    @Test
    fun `isValidCreditLimit returns true for zero and positive values`() {
        assertTrue(ValidationUtils.isValidCreditLimit(0.0))
        assertTrue(ValidationUtils.isValidCreditLimit(1000.0))
        assertTrue(ValidationUtils.isValidCreditLimit(100000.0))
    }

    @Test
    fun `isValidCreditLimit returns false for negative values`() {
        assertFalse(ValidationUtils.isValidCreditLimit(-1.0))
        assertFalse(ValidationUtils.isValidCreditLimit(-1000.0))
    }

    // Credit Days Validation Tests
    @Test
    fun `isValidCreditDays returns true for days between 0 and 365`() {
        assertTrue(ValidationUtils.isValidCreditDays(0))
        assertTrue(ValidationUtils.isValidCreditDays(30))
        assertTrue(ValidationUtils.isValidCreditDays(365))
    }

    @Test
    fun `isValidCreditDays returns false for days outside range`() {
        assertFalse(ValidationUtils.isValidCreditDays(-1))
        assertFalse(ValidationUtils.isValidCreditDays(366))
        assertFalse(ValidationUtils.isValidCreditDays(1000))
    }
}
