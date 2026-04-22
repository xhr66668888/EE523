package com.example.shakecounter

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied - shake detection may not work", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request permission
        checkAndRequestPermission()
        
        setContent {
            ShakeCounterAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShakeCounterScreen()
                }
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
    }
}

@Composable
fun ShakeCounterAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Composable
fun ShakeCounterScreen() {
    val context = LocalContext.current
    val sensorManager = remember { SensorManagerWrapper(context) }
    
    // Check if device has accelerometer
    if (!sensorManager.hasAccelerometer()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No accelerometer found on this device", fontSize = 20.sp)
        }
        return
    }
    
    // Shake count state
    var shakeCount by remember { mutableStateOf(0) }
    // Get accelerometer values
    val (x, y, z) = sensorManager.rememberAccelerometerValues()
    
    // Shake detector
    val shakeDetector = remember {
        ShakeDetector {
            shakeCount++
        }
    }
    
    // Register and unregister sensor listener
    DisposableEffect(Unit) {
        sensorManager.registerShakeListener(shakeDetector)
        onDispose {
            sensorManager.unregisterShakeListener(shakeDetector)
        }
    }
    
    // UI
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Shake Counter",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "$shakeCount",
            fontSize = 96.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "Shake your phone to count!",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        // Accelerometer display
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Accelerometer Values", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("X: ${"%.2f".format(x)}", fontSize = 16.sp)
                Text("Y: ${"%.2f".format(y)}", fontSize = 16.sp)
                Text("Z: ${"%.2f".format(z)}", fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { shakeCount = 0 },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Reset Counter", fontSize = 18.sp)
        }
    }
}