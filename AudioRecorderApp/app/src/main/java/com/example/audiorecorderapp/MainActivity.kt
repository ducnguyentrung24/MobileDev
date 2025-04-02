package com.example.audiorecorderapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var btnRecord: Button
    private lateinit var btnStop: Button
    private lateinit var btnPlay: Button
    private lateinit var listView: ListView
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var filePath: String = ""
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var fileList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnStop = findViewById(R.id.btnStop)
        btnPlay = findViewById(R.id.btnPlay)
        listView = findViewById(R.id.listView)

        checkPermissions()
        setupListeners()
        loadRecordings()
    }

    // Yêu cầu quyền ghi âm
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    // Thiết lập sự kiện cho các nút
    private fun setupListeners() {
        btnRecord.setOnClickListener { startRecording() }
        btnStop.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener { playRecording() }
        listView.setOnItemClickListener { _, _, position, _ ->
            playSelectedRecording(fileList[position])
        }
    }

    // Bắt đầu ghi âm
    private fun startRecording() {
        filePath = "${externalCacheDir?.absolutePath}/recording_${System.currentTimeMillis()}.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
                Log.d("MainActivity", "Bắt đầu ghi âm vào file: $filePath")
                btnRecord.isEnabled = false
                btnStop.isEnabled = true
                Toast.makeText(this@MainActivity, "Đang ghi âm...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "LỖI GHI ÂM: ${e.message}")
            }
        }
    }

    // Dừng ghi âm
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        btnRecord.isEnabled = true
        btnStop.isEnabled = false
        btnPlay.isEnabled = true

        val file = File(filePath)
        if (file.exists()) {
            Log.d("MainActivity", "File ghi âm tồn tại, kích thước: ${file.length()} bytes")
        } else {
            Log.e("MainActivity", "LỖI: File ghi âm không tồn tại!")
        }

        Toast.makeText(this, "Ghi âm hoàn tất!", Toast.LENGTH_SHORT).show()
        loadRecordings()
    }

    // Phát lại ghi âm
    private fun playRecording() {
        val file = File(filePath)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(this, "Tệp ghi âm không tồn tại hoặc rỗng!", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "LỖI: Không tìm thấy file hoặc file rỗng!")
            return
        }
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            try {
                prepare()
                start()
                Log.d("MainActivity", "Đang phát file: $filePath")
                Toast.makeText(this@MainActivity, "Đang phát lại...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "LỖI PHÁT FILE: ${e.message}")
            }
        }
    }

    // Phát lại ghi âm đã chọn
    private fun playSelectedRecording(fileName: String) {
        val selectedFilePath = "${externalCacheDir?.absolutePath}/$fileName"
        val file = File(selectedFilePath)

        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(this, "Tệp ghi âm không tồn tại hoặc rỗng!", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "LỖI: File $fileName không tồn tại hoặc rỗng!")
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(selectedFilePath)
            try {
                prepare()
                start()
                Log.d("MainActivity", "Đang phát file: $selectedFilePath")
                Toast.makeText(this@MainActivity, "Phát lại: $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "LỖI PHÁT FILE: ${e.message}")
            }
        }
    }

    // Hiển thị danh sách file ghi âm
    private fun loadRecordings() {
        val dir = externalCacheDir ?: return
        fileList = ArrayList(dir.list()?.toList() ?: listOf())

        if (fileList.isEmpty()) {
            Log.d("MainActivity", "Không có file ghi âm nào.")
        } else {
            Log.d("MainActivity", "Danh sách file ghi âm: $fileList")
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        listView.adapter = adapter
    }
}
