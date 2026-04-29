package com.example.drawingapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var btnBlack: Button
    private lateinit var btnRed: Button
    private lateinit var btnBlue: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        btnBlack = findViewById(R.id.btnBlack)
        btnRed = findViewById(R.id.btnRed)
        btnBlue = findViewById(R.id.btnBlue)
        btnClear = findViewById(R.id.btnClear)

        btnBlack.setOnClickListener {
            drawingView.setColor(Color.BLACK)
        }

        btnRed.setOnClickListener {
            drawingView.setColor(Color.RED)
        }

        btnBlue.setOnClickListener {
            drawingView.setColor(Color.BLUE)
        }

        btnClear.setOnClickListener {
            drawingView.clearCanvas()
        }
    }
}