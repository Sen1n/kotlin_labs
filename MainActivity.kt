package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etFirst = findViewById<EditText>(R.id.etFirstNumber)
        val etSecond = findViewById<EditText>(R.id.etSecondNumber)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val btnMul = findViewById<Button>(R.id.btnMul)
        val btnDiv = findViewById<Button>(R.id.btnDiv)
        val btnClear = findViewById<Button>(R.id.btnClear)


        fun calculate(operation: String) {
            val num1Str = etFirst.text.toString()
            val num2Str = etSecond.text.toString()

            if (num1Str.isEmpty() || num2Str.isEmpty()) {
                tvResult.text = "Будь ласка, введіть обидва числа"
                Log.w("CalcApp", "спроба обчислення з порожніми полями")
                return
            }

            val num1 = num1Str.toDouble()
            val num2 = num2Str.toDouble()

            Log.d("CalcApp", "виконується операція: $operation над числами $num1 та $num2")

            val result = when (operation) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> {
                    // перевірка ділення на нуль
                    if (num2 == 0.0) {
                        tvResult.text = "Помилка: ділення на нуль неможливе!"
                        Log.w("CalcApp", "спроба ділення на нуль")
                        return
                    }
                    num1 / num2
                }
                else -> 0.0
            }

            val formattedResult = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }

            tvResult.text = "Результат: $formattedResult"
        }

        btnAdd.setOnClickListener { calculate("+") }
        btnSub.setOnClickListener { calculate("-") }
        btnMul.setOnClickListener { calculate("*") }
        btnDiv.setOnClickListener { calculate("/") }

        btnClear.setOnClickListener {
            etFirst.text.clear()
            etSecond.text.clear()
            tvResult.text = "Результат: "
            Log.d("CalcApp", "поля очищено")
        }
    }
}