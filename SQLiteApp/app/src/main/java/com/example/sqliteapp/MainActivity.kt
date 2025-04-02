package com.example.sqliteapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnShow = findViewById<Button>(R.id.btnShow)
        val txtResult = findViewById<TextView>(R.id.tvResult)

        dbHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()
            if (dbHelper.addContact(name, phone)) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnUpdate.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()
            if (dbHelper.updateContact(name, phone)) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            val name = edtName.text.toString()
            if (dbHelper.deleteContact(name)) {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnShow.setOnClickListener {
            val contacts = dbHelper.getAllContacts()
            txtResult.text = if (contacts.isEmpty()) "Không có dữ liệu" else contacts.joinToString("\n")
        }
    }
}
