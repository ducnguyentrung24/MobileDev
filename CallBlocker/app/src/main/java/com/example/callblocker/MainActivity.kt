package com.example.callblocker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnRequestPermission: Button
    private lateinit var btnOpenSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRequestPermission = findViewById(R.id.btnRequestPermission)
        btnOpenSettings = findViewById(R.id.btnOpenSettings)

        // Yêu cầu quyền READ_PHONE_STATE nếu cần
        btnRequestPermission.setOnClickListener {
            requestPhoneStatePermission()
        }

        // Mở cài đặt Call Screening để chọn ứng dụng làm trình chặn cuộc gọi
        btnOpenSettings.setOnClickListener {
            openCallScreeningSettings()
        }
    }

    // Hàm xin quyền READ_PHONE_STATE
    private fun requestPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 100)
        } else {
            Toast.makeText(this, "Quyền đã được cấp", Toast.LENGTH_SHORT).show()
        }
    }

    // Mở cài đặt Call Screening
    private fun openCallScreeningSettings() {
        val intent = Intent("android.settings.ACTION_CALL_SCREENING_SETTINGS")

        // Kiểm tra xem có Activity nào có thể xử lý Intent này không
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Đã chặn số 0384719169", Toast.LENGTH_SHORT).show()
        }

    }
}
