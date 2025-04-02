package com.example.sharedprefapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnShow = findViewById<Button>(R.id.btnShow)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnSave.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                PreferenceHelper.saveUserData(this, username, password)
                tvResult.text = "Đã lưu thành công!"
            } else {
                tvResult.text = "Vui lòng nhập đủ thông tin!"
            }
        }

        btnShow.setOnClickListener {
            val (username, password) = PreferenceHelper.getUserData(this)
            if (username != null && password != null) {
                tvResult.text = "Username: $username\nPassword: $password"
            } else {
                tvResult.text = "Không có dữ liệu!"
            }
        }

        btnDelete.setOnClickListener {
            PreferenceHelper.clearUserData(this)
            tvResult.text = "Đã xóa dữ liệu!"
        }
    }
}
