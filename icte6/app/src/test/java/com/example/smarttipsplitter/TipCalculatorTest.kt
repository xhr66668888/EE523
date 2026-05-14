package com.example.smarttipsplitter

import org.junit.Assert.assertEquals
import org.junit.Test

class TipCalculatorTest {

    @Test
    fun calculate_matchesFigmaExampleValues() {
        val result = TipCalculator.calculate(
            billAmount = 120.00,
            taxPercent = 8.25,
            tipPercent = 15.0,
            peopleCount = 4
        )

        assertEquals(9.90, result.taxAmount, 0.001)
        assertEquals(18.00, result.tipAmount, 0.001)
        assertEquals(147.90, result.finalTotal, 0.001)
        assertEquals(36.975, result.perPerson, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculate_rejectsZeroPeople() {
        TipCalculator.calculate(
            billAmount = 25.0,
            taxPercent = 8.0,
            tipPercent = 15.0,
            peopleCount = 0
        )
    }
}
