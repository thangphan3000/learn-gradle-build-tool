package com.bank.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {
    @Test
    void sumsTwoNumbers() {
        assertEquals(5, Utils.sum(2, 3));
    }
}
