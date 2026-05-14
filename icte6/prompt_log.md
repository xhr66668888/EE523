# ICTE6 - Gemini Prompt Log

## App Idea: Smart Tip Splitter

For this assignment, I built a Kotlin Android app that calculates tax, tip, final total, and per-person split amount for a bill.

---

## Prompt 1: Kotlin Starter Code

**Prompt:**
"Write Kotlin code for an Android tip calculator app. It should take bill amount, tax percent, tip percent, and number of people, then calculate tax amount, tip amount, total, and per person amount."

**What Gemini gave me:**
Gemini gave me a simple `MainActivity.kt` example with `EditText`, `Button`, and `TextView`. It also showed the basic formulas:

```kotlin
val taxAmount = billAmount * taxPercent / 100
val tipAmount = billAmount * tipPercent / 100
val total = billAmount + taxAmount + tipAmount
val perPerson = total / people
```

**Was it useful?**
Yes. The formulas were correct and it helped me start the calculator logic.

**What I changed:**
Gemini's code did not have enough validation. I added checks for empty input, invalid numbers, negative bill amounts, tax percent range, and people count. I also separated the calculation into a `TipCalculator` object so I could test it.

---

## Prompt 2: Figma Layout to Android XML

**Prompt:**
"I have a Figma design for a Smart Tip Splitter app. How can I convert this design into Android XML layout code using Kotlin?"

**What Gemini gave me:**
Gemini suggested using the Figma inspect panel to copy sizes, colors, spacing, and text styles. It also mentioned that some Figma-to-code plugins can generate Android layout code, but the generated code usually needs cleanup. Gemini recommended using `ConstraintLayout` because the Figma design has many fixed positions.

**Was it useful?**
Yes, but only as a guide. It helped me understand how to translate the Figma layout into Android views.

**What I changed:**
I did not directly copy generated code. I manually created `activity_main.xml` with `ConstraintLayout`, rounded drawable backgrounds, input rows, tip percentage buttons, a calculate button, and a result card. I adjusted the layout so it works better on Android screens instead of only matching exact Figma pixels.

---

## Prompt 3: Input Validation

**Prompt:**
"How do I validate EditText input in Kotlin Android for a calculator app? I need to prevent empty input, negative numbers, and division by zero."

**What Gemini gave me:**
Gemini showed how to use `toDoubleOrNull()` and `EditText.error` to check if input is valid. It also suggested showing a Toast message when the user enters invalid data.

**Was it useful?**
Yes. The `toDoubleOrNull()` idea was useful because it prevents the app from crashing when the user types invalid text.

**What I changed:**
I made the validation more specific. The bill amount must be greater than 0, the tax percent must be between 0 and 100, and the number of people cannot go below 1. I also made the minus button stop at 1 person.

---

## Prompt 4: Debugging and Testing

**Prompt:**
"My Android calculator app builds but I want to make sure the math is correct. How can I test a Kotlin calculation function?"

**What Gemini gave me:**
Gemini suggested moving the calculation into a separate Kotlin class or object and writing a JUnit test for known values.

**Was it useful?**
Yes. This made the math easier to test without running the whole app.

**What I changed:**
I created `TipCalculator.kt` and added a unit test using the Figma example values: bill `$120.00`, tax `8.25%`, tip `15%`, and `4` people. The expected result is tax `$9.90`, tip `$18.00`, final total `$147.90`, and per person `$36.98` after currency rounding.

---

## Summary

Gemini helped me with the starter Kotlin code, formulas, layout planning, validation, and testing ideas. I still had to read and edit the code myself because some of the output was too generic and did not fully match my Figma design. I also had to clean up the UI and add stronger validation before the app was complete.
