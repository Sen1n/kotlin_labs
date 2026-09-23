package com.example.myapplication // Убедись, что пакет совпадает с твоим

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Инициализация ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Переменные состояния
    private var currentInput = ""
    private var firstOperand = 0.0
    private var currentOperation = ""
    private var isNewOperation = true // Флаг для очистки экрана при вводе второго числа

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Развертывание layout через ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Слушатели цифр
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                onDigitPressed(button.text.toString())
            }
        }

        // Слушатели операций
        binding.btnPlus.setOnClickListener { onOperationPressed("+") }
        binding.btnMinus.setOnClickListener { onOperationPressed("-") }
        binding.btnMultiply.setOnClickListener { onOperationPressed("*") }
        binding.btnDivide.setOnClickListener { onOperationPressed("/") }

        // Дополнительные функции
        binding.btnEquals.setOnClickListener { calculateResult() }
        binding.btnC.setOnClickListener { clearAll() }
        binding.btnDot.setOnClickListener { onDotPressed() }
        binding.btnBackspace.setOnClickListener { onBackspacePressed() }
    }

    private fun onDigitPressed(digit: String) {
        if (isNewOperation) {
            currentInput = ""
            isNewOperation = false
        }
        currentInput += digit
        updateDisplay(currentInput)
    }

    private fun onOperationPressed(operation: String) {
        if (currentInput.isNotEmpty()) {
            // Цепные вычисления: если операция уже была, считаем промежуточный результат
            if (currentOperation.isNotEmpty() && !isNewOperation) {
                calculateResult()
            } else {
                firstOperand = currentInput.toDoubleOrNull() ?: 0.0
            }
        }
        currentOperation = operation
        isNewOperation = true
    }

    private fun calculateResult() {
        if (currentOperation.isEmpty() || isNewOperation) return

        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        var result = 0.0
        var isError = false

        when (currentOperation) {
            "+" -> result = firstOperand + secondOperand
            "-" -> result = firstOperand - secondOperand
            "*" -> result = firstOperand * secondOperand
            "/" -> {
                if (secondOperand == 0.0) {
                    isError = true
                } else {
                    result = firstOperand / secondOperand
                }
            }
        }

        if (isError) {
            binding.tvDisplay.text = "Помилка"
            currentInput = ""
        } else {
            // Форматирование: убираем .0 если число целое
            currentInput = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }
            updateDisplay(currentInput)
            firstOperand = result // Сохраняем результат для возможных цепных вычислений
        }

        currentOperation = ""
        isNewOperation = true
    }

    private fun onDotPressed() {
        if (isNewOperation) {
            currentInput = "0."
            isNewOperation = false
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
        updateDisplay(currentInput)
    }

    private fun onBackspacePressed() {
        if (currentInput.isNotEmpty() && !isNewOperation) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty() || currentInput == "-") {
                currentInput = "0"
                isNewOperation = true
            }
            updateDisplay(currentInput)
        }
    }

    private fun clearAll() {
        currentInput = ""
        firstOperand = 0.0
        currentOperation = ""
        isNewOperation = true
        updateDisplay("0")
    }

    private fun updateDisplay(text: String) {
        binding.tvDisplay.text = text
    }
}