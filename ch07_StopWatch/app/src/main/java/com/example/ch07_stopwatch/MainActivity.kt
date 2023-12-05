package com.example.ch07_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var isRunning = false

    var timer : Timer? = null
    var time = 0

    private lateinit var btn_start: Button
    private lateinit var btn_init: Button
    private lateinit var tv_min: TextView
    private lateinit var tv_sec: TextView
    private lateinit var tv_msec: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start = findViewById(R.id.btn_start)
        btn_init = findViewById(R.id.btn_init)
        tv_min = findViewById(R.id.tv_min)
        tv_sec = findViewById(R.id.tv_sec)
        tv_msec = findViewById(R.id.tv_msec)

        btn_start.setOnClickListener(this)
        btn_init.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> {
                if (isRunning) {
                    pause()
                } else {
                    start()
                }
            }
            R.id.btn_init -> {
                refresh()
            }
        }
    }

    private fun start() {
        btn_start.text = "일시정지"
        btn_start.setBackgroundColor(getColor(R.color.red))
        isRunning = true

        timer = timer(period = 10) {
            time++

            val msec = time % 100
            val sec = (time % 6000) / 100
            val min = time / 6000

            runOnUiThread {
                if (isRunning) {
                    tv_msec.text = if (msec < 10) ".0${msec}" else "${msec}"
                    tv_sec.text = if (sec < 10) ":0${sec}" else "${sec}"
                    tv_min.text = "${min}"
                }
            }
        }
    }

    private fun pause() {
        btn_start.text = "시작"
        btn_start.setBackgroundColor(getColor(R.color.blue))
        isRunning = false
        timer?.cancel()
    }

    private fun refresh() {
        pause()
        time = 0
        tv_msec.text = ".00"
        tv_sec.text = ":00"
        tv_min.text = "00"
    }
}