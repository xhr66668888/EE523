package com.example.swapsense.ui.dashboard

import android.Manifest
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.swapsense.R
import com.example.swapsense.databinding.FragmentDashboardBinding
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // TextViews to display sensor data
    private val binding get() = _binding!!

    private lateinit var sensorMan: SensorManager

    private var lightSensor: Sensor? = null
    private var proxSensor: Sensor? = null
    private var accelSensor: Sensor? = null

    private lateinit var lightTxt: TextView
    private lateinit var proxTxt: TextView
    private lateinit var accelTxt: TextView

    // permission request code
    private val SENSOR_PERMISSION_CODE = 100

    private val sensorPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var allGranted = true
        for ((key, value) in permissions) {
            if (!value) {
                allGranted = false
                println("sensor permission denied: " + key)
            }
        }
        if (allGranted) {
            setupSensors()
            println("all sensor permissions granted")
        } else {
            Toast.makeText(requireContext(), "Sensor permissions are needed to display sensor data", Toast.LENGTH_LONG).show()
            lightTxt.text = "Light: Permission denied"
            proxTxt.text = "Proximity: Permission denied"
            accelTxt.text = "Accel: Permission denied"
        }
    }

    // permissions needed for body sensors on android 10+
    private val sensorPermissions: Array<String>
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // android 10+ needs BODY_SENSORS
                arrayOf(Manifest.permission.BODY_SENSORS)
            } else {
                // older versions dont need it
                emptyArray()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init the text views
        lightTxt = view.findViewById(R.id.value_light)
        proxTxt = view.findViewById(R.id.value_proximity)
        accelTxt = view.findViewById(R.id.value_accel)

        // get sensor manager
        sensorMan = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // check permissions first
        if (checkSensorPermissions()) {
            setupSensors()
        } else {
            // ask for permissions
            requestSensorPermissions()
        }

    }

    private fun checkSensorPermissions(): Boolean {
        if (sensorPermissions.isEmpty()) {
            return true // no permissions needed
        }
        for (perm in sensorPermissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestSensorPermissions() {
        if (sensorPermissions.isNotEmpty()) {
            // ask user for permission with a message first
            Toast.makeText(requireContext(), "Please allow sensor permissions to view sensor data", Toast.LENGTH_LONG).show()
            sensorPermissionLauncher.launch(sensorPermissions)
        }
    }

    private fun setupSensors() {
        // log all sensors just to see
        var allSensors: List<Sensor> = sensorMan.getSensorList(Sensor.TYPE_ALL)
        for (s in allSensors) {
            Log.d("sensor_debug", "found sensor: " + s.name)
        }

        // get the 3 sensors we want
        lightSensor = sensorMan.getDefaultSensor(Sensor.TYPE_LIGHT)
        proxSensor = sensorMan.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelSensor = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // check if they exist
        if (lightSensor == null) {
            Toast.makeText(requireContext(), "Light sensor not found on this device", Toast.LENGTH_LONG).show()
            println("no light sensor!")
            lightTxt.text = "Light: Not available"
        }
        if (proxSensor == null) {
            Toast.makeText(requireContext(), "Proximity sensor not found on this device", Toast.LENGTH_LONG).show()
            println("no proximity sensor!")
            proxTxt.text = "Proximity: Not available"
        }
        if (accelSensor == null) {
            Toast.makeText(requireContext(), "Accelerometer not found on this device", Toast.LENGTH_LONG).show()
            println("no accel sensor!")
            accelTxt.text = "Accel: Not available"
        }

        // register listeners
        if (lightSensor != null) {
            sensorMan.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (proxSensor != null) {
            sensorMan.registerListener(sensorListener, proxSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (accelSensor != null) {
            sensorMan.registerListener(sensorListener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // sensor event listener for all 3 sensors
    private var sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_LIGHT -> {
                        var lux = it.values[0]
                        // format to 2 decimal places maybe
                        lightTxt.text = "Light: %.2f lux".format(lux)
                    }
                    Sensor.TYPE_PROXIMITY -> {
                        var dist = it.values[0]
                        proxTxt.text = "Proximity: %.2f cm".format(dist)
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        var x = it.values[0]
                        var y = it.values[1]
                        var z = it.values[2]
                        accelTxt.text = "Accel: X=%.2f, Y=%.2f, Z=%.2f".format(x, y, z)
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // dont really need this but have to implement it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // unregister sensors to save battery i think
        sensorMan.unregisterListener(sensorListener)
        _binding = null
    }
}
