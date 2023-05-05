package com.example.blenotify

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.widget.Button


class MainActivity : AppCompatActivity() {

    lateinit var boutonGoBlePage : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        boutonGoBlePage = findViewById(R.id.buttonBluetooth)
        val intent = Intent(this, BleScanActivity::class.java)

        boutonGoBlePage.setOnClickListener{
            startActivity(intent)
        }
    }
}