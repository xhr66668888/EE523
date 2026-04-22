package com.example.shakecounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.*

/**
 * Sensor Manager Wrapper class
 * Encapsulates accelerometer sensor access and lifecycle management
 */
class SensorManagerWrapper(private val context: Context) {
    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    /**
     * Check if device has accelerometer
     */
    fun hasAccelerometer(): Boolean = accelerometer != null

    /**
     * Register shake detection listener
     */
    fun registerShakeListener(shakeDetector: ShakeDetector) {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                shakeDetector,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /**
     * Unregister shake detection listener
     */
    fun unregisterShakeListener(shakeDetector: ShakeDetector) {
        sensorManager.unregisterListener(shakeDetector)
    }

    /**
     * Get current accelerometer values (for display)
     */
    @Composable
    fun rememberAccelerometerValues(): Triple<Float, Float, Float> {
        var x by remember { mutableStateOf(0f) }
        var y by remember { mutableStateOf(0f) }
        var z by remember { mutableStateOf(0f) }
        
        DisposableEffect(Unit) {
            val listener = object : android.hardware.SensorEventListener {
                override fun onSensorChanged(event: android.hardware.SensorEvent?) {
                    event?.let { sensorEvent ->
                        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                            x = sensorEvent.values[0]
                            y = sensorEvent.values[1]
                            z = sensorEvent.values[2]
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            
            accelerometer?.let { sensor ->
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
        
        return Triple(x, y, z)
    }
}