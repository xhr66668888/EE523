# Shake Counter - Build Instructions

## Prerequisites
- Android Studio (latest version recommended)
- Physical Android device with accelerometer
- USB cable to connect device to computer

## Steps to Build and Install

### 1. Open Project in Android Studio
1. Launch Android Studio
2. Click "Open an Existing Project"
3. Navigate to the `ShakeCounterApp` folder
4. Click "OK"

### 2. Wait for Gradle Sync
- Android Studio will automatically sync Gradle files
- This may take a few minutes on first open
- If prompted, click "Sync Now"

### 3. Configure Device
1. Enable Developer Options on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Go to Settings > Developer Options
   - Turn on "USB Debugging"
3. Connect device to computer via USB
4. Allow USB debugging when prompted on device

### 4. Build and Run
1. In Android Studio, click the green "Run" button (or Shift+F10)
2. Select your connected device from the dropdown
3. Click "OK"
4. App will install and launch on your device

### 5. Grant Permission
- When app launches, grant the ACTIVITY_RECOGNITION permission
- This is required for shake detection on Android 10+

## Testing the App

1. **Shake Detection**: Shake your device - the counter should increment
2. **Accelerometer Values**: Watch X, Y, Z values change in real-time
3. **Reset**: Tap "Reset Counter" to reset to zero

## Troubleshooting

### Gradle Sync Fails
- Check internet connection
- Try File > Invalidate Caches / Restart

### Device Not Recognized
- Ensure USB debugging is enabled
- Try a different USB cable or port
- Install device drivers if needed

### App Crashes
- Check if device has accelerometer (most phones do)
- Ensure permission was granted

### Shake Not Detected
- Shake more vigorously
- Check if accelerometer sensor is working (use a sensor test app)
- Adjust SHAKE_THRESHOLD in ShakeDetector.kt if needed

## Project Structure

```
ShakeCounterApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/shakecounter/
│           │   ├── MainActivity.kt
│           │   ├── ShakeDetector.kt
│           │   └── SensorManagerWrapper.kt
│           ├── res/values/
│           │   └── strings.xml
│           └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    ├── wrapper/
    │   └── gradle-wrapper.properties
    └── libs.versions.toml
```