# ICTE4 - Gemini Prompt Log

## App Idea: Shake Counter
An app that uses the accelerometer sensor to detect device shakes and count them.

---

## Prompt 1: Sensor Access

**My Question:**
"How do I access the accelerometer in a Kotlin Android Compose app?"

**Gemini's Response:**
```kotlin
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import androidx.compose.runtime.*

@Composable
fun AccelerometerSensor(context: Context) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                // Handle sensor data
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}
```

**Evaluation:**
- Helpful: Provided basic sensor access code structure
- Issues: Didn't handle permission requests, didn't show how to use state management in Compose

---

## Prompt 2: Shake Detection UI

**My Question:**
"Create a Compose UI that detects device shakes, displays shake count and real-time acceleration values"

**Gemini's Response:**
```kotlin
@Composable
fun ShakeDetectorScreen() {
    var shakeCount by remember { mutableStateOf(0) }
    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }
    var z by remember { mutableStateOf(0f) }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Shake Count: $shakeCount", fontSize = 48.sp)
        Text("X: $x")
        Text("Y: $y") 
        Text("Z: $z")
        Button(onClick = { shakeCount = 0 }) {
            Text("Reset")
        }
    }
}
```

**Evaluation:**
- Helpful: Provided basic UI structure
- Issues: No actual sensor integration, no shake detection algorithm

---

## Prompt 3: Lifecycle Handling

**My Question:**
"How do I properly handle sensor listener lifecycle, registering/unregistering in onPause and onResume?"

**Gemini's Response:**
```kotlin
@Composable
fun SensorScreen(context: Context) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                // Handle sensor event
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}
```

**Evaluation:**
- Helpful: Correctly used DisposableEffect for lifecycle management
- Issues: Didn't mention the need for ACTIVITY_RECOGNITION permission, no debounce logic

---

## Prompt 4: Permission Request (My Additional Question)

**My Question:**
"What permissions are needed for shake detection?"

**Gemini's Response:**
"Add to AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

For Android 10+, you need to request this permission at runtime."

**Evaluation:**
- Helpful: Correctly identified the required permission
- Issues: Didn't provide complete runtime permission request code

---

## Summary

**How Gemini Helped:**
1. Provided basic sensor access code structure
2. Provided Compose UI templates
3. Correctly identified the need for ACTIVITY_RECOGNITION permission

**What Required Human Correction:**
1. Missing complete permission request flow
2. No debounce logic, causing repeated counting
3. Sensor sampling rate may not be correct
4. Missing error handling mechanisms