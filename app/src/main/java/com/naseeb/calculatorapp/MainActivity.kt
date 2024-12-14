package com.naseeb.calculatorapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var historyDisplay: TextView
    private lateinit var buttonConv: Button
    private lateinit var buttonSin: Button
    private lateinit var buttonCos: Button
    private lateinit var buttonTan: Button
    private lateinit var buttonLog: Button
    private lateinit var buttonLn: Button
    private lateinit var buttonLg: Button
    private lateinit var buttonPower: Button
    private lateinit var buttonFactorial: Button
    private lateinit var buttonSqrt: Button
    private lateinit var buttonPi: Button
    private lateinit var buttonOpenParenthesis: Button
    private lateinit var buttonCloseParenthesis: Button
    private lateinit var buttonBackspace: Button

    private lateinit var numberButtons: Array<Button>
    private lateinit var operatorButtons: Array<Button>

    private var isAdvancedMode = false

    private lateinit var history: TextView
    private lateinit var database: CalculatorDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Change the status bar color
        changeStatusBarColor()

        // Initialize views
        display = findViewById(R.id.display)
        historyDisplay = findViewById(R.id.historyDisplay)
        buttonConv = findViewById(R.id.buttonConv)
        history = findViewById(R.id.history)
        database = CalculatorDatabase.getDatabase(this)

        // Initialize advanced operation buttons
        buttonSin = findViewById(R.id.buttonSin)
        buttonCos = findViewById(R.id.buttonCos)
        buttonTan = findViewById(R.id.buttonTan)
        buttonLog = findViewById(R.id.buttonLog)
        buttonLn = findViewById(R.id.buttonLn)
        buttonLg = findViewById(R.id.buttonLg)
        buttonPower = findViewById(R.id.buttonPower)
        buttonFactorial = findViewById(R.id.buttonFactorial)
        buttonSqrt = findViewById(R.id.buttonSqrt)
        buttonPi = findViewById(R.id.buttonPi)
        buttonOpenParenthesis = findViewById(R.id.buttonOpenParenthesis)
        buttonCloseParenthesis = findViewById(R.id.buttonCloseParenthesis)
        buttonBackspace = findViewById(R.id.buttonBackspace)

        // Initialize number buttons
        numberButtons = Array(10) { i ->
            findViewById(resources.getIdentifier("button$i", "id", packageName))
        }


        // Show history on TextView click
        history.setOnClickListener {
            showHistory()
        }

        // Initialize operator buttons
        operatorButtons = arrayOf(
            findViewById(R.id.buttonAdd),
            findViewById(R.id.buttonSubtract),
            findViewById(R.id.Multiply),
            findViewById(R.id.Divide),
            findViewById(R.id.buttonEquals),
            findViewById(R.id.buttonClear),
            findViewById(R.id.buttonDecimal),
            findViewById(R.id.buttonPercentage)
        )

        // Initially hide advanced buttons
        hideAdvancedButtons()

        // Toggle advanced buttons when "Conv" is clicked
        buttonConv.setOnClickListener {
            if (isAdvancedMode) hideAdvancedButtons() else showAdvancedButtons()
            isAdvancedMode = !isAdvancedMode
        }

        // Add number button listeners
        for (i in 0..9) {
            numberButtons[i].setOnClickListener {
                display.append(i.toString())
            }
        }

        // Add operator button listeners
        for (button in operatorButtons) {
            button.setOnClickListener {
                handleOperator(button.text.toString())
            }
        }

        // Add advanced operation button listeners
        buttonSin.setOnClickListener { performAdvancedOperation("sin") }
        buttonCos.setOnClickListener { performAdvancedOperation("cos") }
        buttonTan.setOnClickListener { performAdvancedOperation("tan") }
        buttonLog.setOnClickListener { performAdvancedOperation("log") }
        buttonLn.setOnClickListener { performAdvancedOperation("ln") }
        buttonLg.setOnClickListener { performAdvancedOperation("lg") }
        buttonPower.setOnClickListener { display.append("^") }
        buttonFactorial.setOnClickListener { performAdvancedOperation("x!") }
        buttonSqrt.setOnClickListener { performAdvancedOperation("√x") }
        buttonPi.setOnClickListener { display.append(Math.PI.toString()) }
        buttonOpenParenthesis.setOnClickListener { display.append("(") }
        buttonCloseParenthesis.setOnClickListener { display.append(")") }
        buttonBackspace.setOnClickListener {
            val currentText = display.text.toString()
            if (currentText.isNotEmpty()) {
                display.text = currentText.substring(0, currentText.length - 1)
            }
        }
    }

    private fun handleOperator(operator: String) {
        when (operator) {
            "+" -> display.append("+")
            "-" -> display.append("-")
            "*" -> display.append("*")
            "/" -> display.append("/")
            "%" -> {
                try {
                    val input = display.text.toString().toDouble()
                    display.text = (input / 100).toString()
                } catch (e: Exception) {
                    display.text = "Error"
                }
            }
            "=" -> {
                try {
                    val expression = display.text.toString()
                    val result = eval(expression).toString()

                    // Save to Room DB
                    saveToHistory(expression, result)
                    display.text = result.toString()
                } catch (e: Exception) {
                    display.text = "Error"
                }
            }
            "C" -> display.text = ""
            "." -> display.append(".")
        }
    }

    private fun performAdvancedOperation(operation: String) {
        val input = display.text.toString()
        try {
            val result = when (operation) {
                "sin" -> sin(Math.toRadians(input.toDouble()))
                "cos" -> cos(Math.toRadians(input.toDouble()))
                "tan" -> tan(Math.toRadians(input.toDouble()))
                "log" -> log10(input.toDouble())
                "ln" -> ln(input.toDouble())
                "x!" -> factorial(input.toInt())
                "√x" -> sqrt(input.toDouble())
                else -> throw IllegalArgumentException("Unknown operation")
            }
            display.text = result.toString()
        } catch (e: Exception) {
            display.text = "Error"
        }
    }

    private fun factorial(n: Int): Double {
        return if (n == 0) 1.0 else (1..n).fold(1L) { acc, i -> acc * i }.toDouble()
    }

    private fun eval(expr: String): Double {
        return try {
            ExpressionBuilder(expr).build().evaluate()
        } catch (e: Exception) {
            Double.NaN
        }
    }


    private fun hideAdvancedButtons() {
        buttonSin.visibility = View.GONE
        buttonCos.visibility = View.GONE
        buttonTan.visibility = View.GONE
        buttonLn.visibility = View.GONE
        buttonLog.visibility = View.GONE
        buttonPi.visibility = View.GONE
        buttonSqrt.visibility = View.GONE
        buttonCloseParenthesis.visibility = View.GONE
        buttonOpenParenthesis.visibility = View.GONE
        buttonFactorial.visibility = View.GONE
        buttonPower.visibility = View.GONE
        buttonLg.visibility = View.GONE
    }

    private fun showAdvancedButtons() {
        buttonSin.visibility = View.VISIBLE
        buttonCos.visibility = View.VISIBLE
        buttonTan.visibility = View.VISIBLE
        buttonLn.visibility = View.VISIBLE
        buttonLog.visibility = View.VISIBLE
        buttonPi.visibility = View.VISIBLE
        buttonSqrt.visibility = View.VISIBLE
        buttonCloseParenthesis.visibility = View.VISIBLE
        buttonOpenParenthesis.visibility = View.VISIBLE
        buttonFactorial.visibility = View.VISIBLE
        buttonPower.visibility = View.VISIBLE
        buttonLg.visibility = View.VISIBLE
    }

    private fun changeStatusBarColor() {
        val window: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.status_bar_color)
        }
    }
    private fun saveToHistory(expression: String, result: String) {
        lifecycleScope.launch {
            database.historyDao().insert(HistoryEntity(expression = expression, result = result))
        }
    }

    private fun showHistory() {
        lifecycleScope.launch {
            val history = database.historyDao().getAllHistory()

            val historyList = history.joinToString("\n") { "${it.expression} = ${it.result}" }

            // Find the TextView by its ID
            val historyTextView = findViewById<TextView>(R.id.historyDisplay)

            // Set the history in the TextView
            historyTextView.text = if (historyList.isEmpty()) {
                "No history available"
            } else {
                historyList
            }

            // Enable scrolling by ensuring the ScrollView is used
            val scrollView = findViewById<ScrollView>(R.id.historyScrollView)
            scrollView.visibility = View.VISIBLE
        }
    }


    private fun clearHistory() {
        lifecycleScope.launch {
            database.historyDao().clearHistory()
            Toast.makeText(this@MainActivity, "History cleared", Toast.LENGTH_SHORT).show()
        }
    }


}
