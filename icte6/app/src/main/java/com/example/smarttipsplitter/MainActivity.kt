package com.example.smarttipsplitter

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var billInput: EditText
    private lateinit var taxInput: EditText
    private lateinit var peopleCountText: TextView
    private lateinit var taxResultText: TextView
    private lateinit var tipResultText: TextView
    private lateinit var totalResultText: TextView
    private lateinit var personResultText: TextView
    private lateinit var tipButtons: Map<TextView, Double>

    private val moneyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private var selectedTipPercent = 15.0
    private var peopleCount = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setContentView(R.layout.activity_main)

        bindViews()
        configureTipButtons()
        configureActions()
        updatePeopleCount()
        selectTip(15.0)
        calculateAndShowResults()
    }

    private fun bindViews() {
        billInput = findViewById(R.id.billInput)
        taxInput = findViewById(R.id.taxInput)
        peopleCountText = findViewById(R.id.peopleCountText)
        taxResultText = findViewById(R.id.taxResultText)
        tipResultText = findViewById(R.id.tipResultText)
        totalResultText = findViewById(R.id.totalResultText)
        personResultText = findViewById(R.id.personResultText)
    }

    private fun configureTipButtons() {
        tipButtons = linkedMapOf(
            findViewById<TextView>(R.id.tip10Button) to 10.0,
            findViewById<TextView>(R.id.tip12Button) to 12.0,
            findViewById<TextView>(R.id.tip15Button) to 15.0,
            findViewById<TextView>(R.id.tip18Button) to 18.0,
            findViewById<TextView>(R.id.tip20Button) to 20.0
        )

        tipButtons.forEach { (button, percent) ->
            button.setOnClickListener {
                selectTip(percent)
                calculateAndShowResults()
            }
        }
    }

    private fun configureActions() {
        findViewById<Button>(R.id.calculateButton).setOnClickListener {
            calculateAndShowResults()
            hideKeyboard()
        }

        findViewById<TextView>(R.id.increasePeopleButton).setOnClickListener {
            if (peopleCount < 99) {
                peopleCount += 1
                updatePeopleCount()
                calculateAndShowResults()
            }
        }

        findViewById<TextView>(R.id.decreasePeopleButton).setOnClickListener {
            if (peopleCount > 1) {
                peopleCount -= 1
                updatePeopleCount()
                calculateAndShowResults()
            } else {
                Toast.makeText(this, "At least 1 person is required.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.resetText).setOnClickListener {
            resetForm()
        }
    }

    private fun selectTip(percent: Double) {
        selectedTipPercent = percent

        tipButtons.forEach { (button, value) ->
            val isSelected = value == percent
            button.setBackgroundResource(
                if (isSelected) R.drawable.bg_tip_selected else R.drawable.bg_tip_unselected
            )
            button.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.white else R.color.black
                )
            )
            button.alpha = if (isSelected) 1.0f else 0.7f
        }
    }

    private fun updatePeopleCount() {
        peopleCountText.text = peopleCount.toString()
    }

    private fun resetForm() {
        billInput.setText("120.00")
        taxInput.setText("8.25")
        billInput.error = null
        taxInput.error = null
        peopleCount = 4
        updatePeopleCount()
        selectTip(15.0)
        calculateAndShowResults()
        hideKeyboard()
    }

    private fun calculateAndShowResults(): Boolean {
        val billAmount = readMoneyInput(billInput, "Enter a bill amount greater than 0.")
            ?: return false
        val taxPercent = readPercentInput(taxInput)
            ?: return false

        val result = TipCalculator.calculate(
            billAmount = billAmount,
            taxPercent = taxPercent,
            tipPercent = selectedTipPercent,
            peopleCount = peopleCount
        )

        taxResultText.text = moneyFormat.format(result.taxAmount)
        tipResultText.text = moneyFormat.format(result.tipAmount)
        totalResultText.text = moneyFormat.format(result.finalTotal)
        personResultText.text = moneyFormat.format(result.perPerson)
        return true
    }

    private fun readMoneyInput(input: EditText, errorMessage: String): Double? {
        val amount = input.text.toString().trim().toDoubleOrNull()
        return when {
            amount == null || amount <= 0.0 -> {
                showInputError(input, errorMessage)
                null
            }
            else -> {
                input.error = null
                amount
            }
        }
    }

    private fun readPercentInput(input: EditText): Double? {
        val percent = input.text.toString().trim().toDoubleOrNull()
        return when {
            percent == null -> {
                showInputError(input, "Enter a valid tax percentage.")
                null
            }
            percent < 0.0 || percent > 100.0 -> {
                showInputError(input, "Tax percentage must be between 0 and 100.")
                null
            }
            else -> {
                input.error = null
                percent
            }
        }
    }

    private fun showInputError(input: EditText, message: String) {
        input.error = message
        input.requestFocus()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val focusedView = currentFocus ?: return
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
        focusedView.clearFocus()
    }
}
