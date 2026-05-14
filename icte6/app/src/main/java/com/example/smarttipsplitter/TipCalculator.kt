package com.example.smarttipsplitter

data class TipResult(
    val taxAmount: Double,
    val tipAmount: Double,
    val finalTotal: Double,
    val perPerson: Double
)

object TipCalculator {
    fun calculate(
        billAmount: Double,
        taxPercent: Double,
        tipPercent: Double,
        peopleCount: Int
    ): TipResult {
        require(billAmount >= 0.0) { "Bill amount cannot be negative." }
        require(taxPercent in 0.0..100.0) { "Tax percent must be between 0 and 100." }
        require(tipPercent in 0.0..100.0) { "Tip percent must be between 0 and 100." }
        require(peopleCount > 0) { "People count must be at least 1." }

        val taxAmount = billAmount * taxPercent / 100.0
        val tipAmount = billAmount * tipPercent / 100.0
        val finalTotal = billAmount + taxAmount + tipAmount

        return TipResult(
            taxAmount = taxAmount,
            tipAmount = tipAmount,
            finalTotal = finalTotal,
            perPerson = finalTotal / peopleCount
        )
    }
}
