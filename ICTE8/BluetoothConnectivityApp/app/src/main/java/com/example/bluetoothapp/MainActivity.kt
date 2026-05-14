package com.example.bluetoothapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.UUID
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    private lateinit var tvStatus: TextView
    private lateinit var btnCheckBluetooth: Button
    private lateinit var btnShowDevices: Button
    private lateinit var tvDevicesHeader: TextView
    private lateinit var lvDevices: ListView

    private val deviceList = mutableListOf<BluetoothDevice>()
    private lateinit var deviceAdapter: DeviceAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                showPairedDevices()
            } else {
                updateStatus(getString(R.string.status_permission_denied))
            }
        }

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                updateStatus(getString(R.string.status_available))
                showPairedDevices()
            } else {
                updateStatus(getString(R.string.status_not_enabled))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnCheckBluetooth = findViewById(R.id.btnCheckBluetooth)
        btnShowDevices = findViewById(R.id.btnShowDevices)
        tvDevicesHeader = findViewById(R.id.tvDevicesHeader)
        lvDevices = findViewById(R.id.lvDevices)

        deviceAdapter = DeviceAdapter(deviceList)
        lvDevices.adapter = deviceAdapter

        btnCheckBluetooth.setOnClickListener { checkBluetooth() }
        btnShowDevices.setOnClickListener { checkPermissionsAndShowDevices() }

        lvDevices.setOnItemClickListener { _, _, position, _ ->
            val device = deviceList[position]
            connectToDevice(device)
        }

        checkBluetooth()
    }

    private fun checkBluetooth() {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            updateStatus(getString(R.string.status_not_supported))
            btnShowDevices.isEnabled = false
        } else {
            updateStatus(getString(R.string.status_available))
            btnShowDevices.isEnabled = true
        }
    }

    private fun checkPermissionsAndShowDevices() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            updateStatus(getString(R.string.status_not_supported))
            return
        }

        if (!adapter.isEnabled) {
            updateStatus(getString(R.string.status_not_enabled))
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableIntent)
            return
        }

        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        } else {
            showPairedDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevices() {
        if (!hasConnectPermission()) {
            updateStatus(getString(R.string.status_permission_denied))
            return
        }

        val pairedDevices = bluetoothAdapter?.bondedDevices
        deviceList.clear()

        if (pairedDevices.isNullOrEmpty()) {
            updateStatus(getString(R.string.status_no_devices))
            tvDevicesHeader.visibility = View.GONE
        } else {
            updateStatus(getString(R.string.status_showing_devices))
            tvDevicesHeader.visibility = View.VISIBLE
            for (device in pairedDevices) {
                deviceList.add(device)
            }
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (!hasConnectPermission()) {
            updateStatus(getString(R.string.status_permission_denied))
            return
        }

        val deviceName = device.name ?: "Unknown Device"
        updateStatus(getString(R.string.status_connecting, deviceName))

        thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                socket.connect()
                runOnUiThread {
                    updateStatus(getString(R.string.status_connected, deviceName))
                }
                socket.close()
            } catch (e: Exception) {
                runOnUiThread {
                    updateStatus(getString(R.string.status_connection_failed))
                }
            }
        }
    }

    private fun hasConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun updateStatus(message: String) {
        tvStatus.text = message
    }

    private inner class DeviceAdapter(private val devices: List<BluetoothDevice>) : BaseAdapter() {

        override fun getCount(): Int = devices.size

        override fun getItem(position: Int): BluetoothDevice = devices[position]

        override fun getItemId(position: Int): Long = position.toLong()

        @SuppressLint("MissingPermission")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View
            val nameView: TextView
            val addressView: TextView

            if (convertView == null) {
                view = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)
                nameView = view.findViewById(android.R.id.text1)
                addressView = view.findViewById(android.R.id.text2)
                view.tag = Pair(nameView, addressView)

                nameView.setTextColor(Color.BLACK)
                addressView.setTextColor(Color.DKGRAY)
            } else {
                view = convertView
                val pair = view.tag as Pair<*, *>
                nameView = pair.first as TextView
                addressView = pair.second as TextView
            }

            val device = devices[position]
            nameView.text = device.name ?: "Unknown Device"
            addressView.text = device.address ?: "Unknown Address"

            return view
        }
    }
}
