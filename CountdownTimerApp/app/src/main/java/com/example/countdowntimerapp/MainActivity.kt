package com.example.countdowntimerapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.countdowntimerapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private var timeElapsed = 0 // Thời gian đã trôi qua
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimer = findViewById(R.id.tvTimer)

        // Bắt đầu thread đếm thời gian
        startTimerThread()
    }

    private fun startTimerThread() {
        Thread {
            while (true) { // Vòng lặp vô hạn để đếm thời gian
                Thread.sleep(1000) // Dừng 1 giây
                timeElapsed++ // Tăng thời gian

                // Cập nhật giao diện qua Handler
                handler.post {
                    tvTimer.text = "$timeElapsed giây"
                }
            }
        }.start()
    }
}
