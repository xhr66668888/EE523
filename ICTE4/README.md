# Shake Counter App

An Android app that uses the accelerometer sensor to detect device shakes and count them.

## Features

- Real-time shake detection using accelerometer
- Shake counter with reset functionality
- Live accelerometer values (X, Y, Z) display
- Modern Material Design UI

## Requirements

- Android device with accelerometer
- Android 7.0 (API 24) or higher
- For Android 10+: ACTIVITY_RECOGNITION permission

## How to Build

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project (Build > Make Project)
4. Run on a physical device (emulators don't have accelerometer)

## How to Use

1. Launch the app
2. Grant the ACTIVITY_RECOGNITION permission when prompted
3. Shake your device to increment the counter
4. View real-time accelerometer values in the card
5. Tap "Reset Counter" to reset the count to zero

## Project Structure

```
ShakeCounterApp/
├── app/src/main/
│   ├── java/com/example/shakecounter/
│   │   ├── MainActivity.kt        # Main activity with Compose UI
│   │   ├── ShakeDetector.kt       # Shake detection algorithm
│   │   └── SensorManagerWrapper.kt # Sensor management wrapper
│   ├── res/values/
│   │   └── strings.xml            # String resources
│   └── AndroidManifest.xml        # App manifest with permissions
├── build.gradle.kts               # App-level build config
└── gradle/                        # Gradle wrapper files
```

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Sensor**: TYPE_ACCELEROMETER
- **Shake Detection**: Based on acceleration threshold (12.0) with 500ms cooldown
- **Permissions**: ACTIVITY_RECOGNITION (Android 10+)

## ICTE4 Deliverables

This project includes:
1. Working Android app prototype
2. Prompt log (`prompt_log.md`) - AI interaction record
3. Reflection (`reflection.md`) - Critical evaluation of AI assistance