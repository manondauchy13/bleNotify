package com.example.blenotify

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

@SuppressLint("MissingPermission")
class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var deviceNameTextView: TextView
    private lateinit var deviceAddressTextView: TextView
    private lateinit var readButton: Button
    private lateinit var writeButton: Button
    private lateinit var notifyButton: Button

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothGatt: BluetoothGatt
    private lateinit var readCharacteristic: BluetoothGattCharacteristic
    private lateinit var writeCharacteristic: BluetoothGattCharacteristic
    private lateinit var notifyCharacteristic: BluetoothGattCharacteristic

    private var isReadPending = false
    private var isWritePending = false
    private var isNotifyPending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)
        val deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
        if(deviceAddress == null) {
            throw IllegalArgumentException("Device address is null")
            Toast.makeText(this, "Device address is null", Toast.LENGTH_SHORT).show()

        }
        // Get the device name and address from the intent
        val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)


        // Set up the UI elements
        deviceNameTextView = findViewById(R.id.device_name_textview)
        deviceAddressTextView = findViewById(R.id.device_address_textview)
        readButton = findViewById(R.id.read_button)
        writeButton = findViewById(R.id.write_button)
        notifyButton = findViewById(R.id.notify_button)

        deviceNameTextView.text = deviceName
        deviceAddressTextView.text = deviceAddress

        // Get the BluetoothManager and BluetoothDevice
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothDevice = bluetoothManager.adapter.getRemoteDevice(deviceAddress)

        // Connect to the device and discover services
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, gattCallback)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) { // Vous avez oublié de fermer la condition if
            bluetoothGatt.close()
        }
    }
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server")

                // Discover services

                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server")
                runOnUiThread {
                    Toast.makeText(
                        this@DeviceDetailActivity,
                        "Disconnected from device",
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered")

                // Find the read, write, and notify characteristics
                val service = gatt.getService(UUID.fromString(SERVICE_UUID))
                readCharacteristic =
                    service.getCharacteristic(UUID.fromString(READ_CHARACTERISTIC_UUID))
                writeCharacteristic =
                    service.getCharacteristic(UUID.fromString(WRITE_CHARACTERISTIC_UUID))
                notifyCharacteristic =
                    service.getCharacteristic(UUID.fromString(NOTIFY_CHARACTERISTIC_UUID))
                // Enable notifications for the notify characteristic

                gatt.setCharacteristicNotification(notifyCharacteristic, true)

                // Write a test value to the write characteristic
                writeCharacteristic.value = "Test".toByteArray()
                gatt.writeCharacteristic(writeCharacteristic)

                // Enable buttons
                runOnUiThread {
                    readButton.isEnabled = true
                    writeButton.isEnabled = true
                    notifyButton.isEnabled = true
                }
            } else {
                Log.d(TAG, "Failed to discover services")
                runOnUiThread {
                    Toast.makeText(
                        this@DeviceDetailActivity,
                        "Failed to discover services",
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Read characteristic value: ${String(characteristic.value)}")
                isReadPending = false
            } else {
                Log.d(TAG, "Failed to read characteristic value")
                isReadPending = false
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Wrote characteristic value: ${String(characteristic.value)}")
                isWritePending = false
            } else {
                Log.d(TAG, "Failed to write characteristic value")
                isWritePending = false
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            Log.d(TAG, "Notify characteristic value changed: ${String(characteristic.value)}")
            isNotifyPending = false
        }
    }

    companion object {
        private const val TAG = "DeviceDetailActivity"
        const val EXTRA_DEVICE_NAME = "extra_device_name"
        const val EXTRA_DEVICE_ADDRESS = "extra_device_address"
        const val SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb"
        const val READ_CHARACTERISTIC_UUID = "00002a19-0000-1000-8000-00805f9b34fb"
        const val WRITE_CHARACTERISTIC_UUID = "00002a38-0000-1000-8000-00805f9b34fb"
        const val NOTIFY_CHARACTERISTIC_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
    }
}