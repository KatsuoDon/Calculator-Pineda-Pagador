package com.example.calculatorpinedapagador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    // This function handles the input of number and decimal button clicks
    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingsTV.append(view.text)

                canAddDecimal = false
            } else {
                workingsTV.append(view.text)
            }
            canAddOperation = true
        }
    }

    // This function handles the input of operation button clicks
    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    // This function clears all inputs and results
    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    // This function removes the last character from the current input
    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0) {
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
        }
    }

    // This function calculates and displays the result of the current input
    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
    }

    // This function calculates the result of the arithmetic expression
    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""
        // This fixes the "Infinity" result issue, it returns Can't divide by zero whenever it sees that the number is divided by zero
        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.contains("Can't divide by zero")) return "Can't divide by zero"

        return try {
            val result = addSubtractCalculate(timesDivision)
            result.toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    // This function performs addition and subtraction calculations
    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '+' -> result += nextDigit
                    '-' -> result -= nextDigit
                }
            }
        }
        return result
    }

    // This function handles multiplication and division operations
    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('÷')) {
            list = calcTimesDiv(list)
            if (list.contains("Can't divide by zero")) {
                return list
            }
        }
        return list
    }

    // This function performs multiplication and division calculations
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '÷' -> {
                        if (nextDigit == 0f) {
                            return mutableListOf("Can't divide by zero")
                        }
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    // This function converts the input string into a list of digits and operators
    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }

        return list
    }
}
