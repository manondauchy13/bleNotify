package com.example.blenotify

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blenotify.R

internal class BleScanAdapter(val bleList: ArrayList<ScanResult>, val clickListener: (BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<BleScanAdapter.BLEViewHolder>() {

    internal inner class BLEViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var blecircle: TextView = view.findViewById(R.id.circle)
        var bledeviceTextView: TextView = view.findViewById(R.id.deviceTextView)
        var blemacTextView: TextView = view.findViewById(R.id.macTextView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BLEViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device, parent, false)
        return BLEViewHolder(itemView)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BLEViewHolder, position: Int) {
        val result = bleList[position]
        holder.blecircle.text = result.rssi.toString()
        holder.blemacTextView.text = result.device.address
        holder.bledeviceTextView.text = result.device.name

        holder.itemView.setOnClickListener{
            clickListener(result.device)
        }
    }

    fun addElement(scanResult : ScanResult) {
        val indexOfResult = bleList.indexOfFirst{
            it.device.address == scanResult.device.address
        }
        if (indexOfResult != -1) {
            bleList[indexOfResult] = scanResult
            notifyItemInserted(indexOfResult)
        }
        else{
            bleList.add(scanResult)
        }
    }
    override fun getItemCount(): Int {
        return bleList.size
    }
}