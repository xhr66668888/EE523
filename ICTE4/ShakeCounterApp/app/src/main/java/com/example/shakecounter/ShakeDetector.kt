package com.example.shakecounter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Shake Detector class
 * Detects device shakes using accelerometer sensor
 */
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    companion object {
        // Shake detection threshold - adjust based on actual testing
        private const val SHAKE_THRESHOLD = 12.0f
        // Debounce time interval (milliseconds)
        private const val SHAKE_COOLDOWN = 500L
        // Last shake timestamp
        private var lastShakeTime = 0L
    }

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var lastUpdate = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val currentTime = System.currentTimeMillis()
                val diffTime = currentTime - lastUpdate
                
                if (diffTime > 100) { // Update every 100ms
                    val x = sensorEvent.values[0]
                    val y = sensorEvent.values[1]
                    val z = sensorEvent.values[2]
                    
                    // Calculate acceleration change
                    val deltaX = x - lastX
                    val deltaY = y - lastY
                    val deltaZ = z - lastZ
                    
                    // Calculate total acceleration change
                    val acceleration = sqrt(
                        deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
                    )
                    
                    // Detect shake
                    if (acceleration > SHAKE_THRESHOLD) {
                        val now = System.currentTimeMillis()
                        if (now - lastShakeTime > SHAKE_COOLDOWN) {
                            lastShakeTime = now
                            onShake.invoke()
                        }
                    }
                    
                    lastX = x
                    lastY = y
                    lastZ = z
                    lastUpdate = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need to handle accuracy changes
    }
}