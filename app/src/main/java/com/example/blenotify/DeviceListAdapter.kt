package com.example.blenotify

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("MissingPermission")

class DeviceListAdapter(private val deviceList: List<BluetoothDevice>,
                        private val listener: OnItemClickListener) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = deviceList[position]

        holder.deviceNameTextView.text = device.name
        holder.connectButton.setOnClickListener { listener.onItemClick(position) }
    }

    override fun getItemCount() = deviceList.size

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceTextView)
        val connectButton: Button

        init {
            itemView.setOnClickListener { listener.onItemClick(adapterPosition) }
            connectButton = itemView.findViewById(R.id.connect_button)
        }
    }
}