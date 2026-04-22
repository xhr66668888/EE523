# ICTE4 Deliverables Summary

## App: Shake Counter

**Feature**: Accelerometer-based shake detection and counting

## Deliverables Checklist

### 1. App Prototype
- [x] Working Android app (ShakeCounterApp/)
- [x] Written in Kotlin
- [x] Uses accelerometer sensor
- [x] Displays shake count and live sensor values
- [x] Includes reset button

**To create video demo**:
1. Build and install on physical Android device
2. Open Shake Counter app
3. Shake device to show counter incrementing
4. Show accelerometer values changing in real-time
5. Tap reset button to demonstrate reset functionality

### 2. Prompt Log
- [x] File: `prompt_log.md`
- [x] Contains 4 prompts with Gemini responses
- [x] Each prompt includes evaluation

**Prompts recorded**:
1. Sensor access in Kotlin Compose
2. Shake detection UI creation
3. Lifecycle handling for sensors
4. Permission requirements

### 3. Reflection
- [x] File: `reflection.md`
- [x] Answers all required questions
- [x] Identifies what Gemini did well
- [x] Identifies errors that needed correction
- [x] Documents changes made

**Key points**:
- Gemini helped with code structure and UI templates
- Gemini missed permissions, debounce logic, and error handling
- We added complete permission flow, shake detection algorithm, and lifecycle management

### 4. In-Class Demo
- [ ] Ready to demonstrate to instructor/TA
- [ ] App installed on physical device
- [ ] Can show shake detection working

## Project Files

```
ICTE4/
├── README.md                              # Project overview
├── prompt_log.md                          # AI interaction record (Part 2 & 3)
├── reflection.md                          # Critical evaluation (Part 5)
├── ICTE4_DELIVERABLES.md                  # This file
└── ShakeCounterApp/                       # Android project (Part 4)
    ├── app/src/main/
    │   ├── java/com/example/shakecounter/
    │   │   ├── MainActivity.kt            # Main activity with Compose UI
    │   │   ├── ShakeDetector.kt           # Shake detection algorithm
    │   │   └── SensorManagerWrapper.kt    # Sensor management wrapper
    │   ├── res/values/
    │   │   └── strings.xml                # String resources
    │   └── AndroidManifest.xml            # App manifest with permissions
    ├── build.gradle.kts                   # App-level build config
    ├── settings.gradle.kts                # Project settings
    ├── gradle.properties                  # Gradle properties
    └── gradle/
        ├── wrapper/
        │   └── gradle-wrapper.properties  # Gradle wrapper config
        └── libs.versions.toml             # Version catalog
```

## How to Build and Run

1. Open Android Studio
2. Open project `ShakeCounterApp/`
3. Wait for Gradle sync
4. Connect physical Android device
5. Click Run (or Build > Make Project)
6. App will install on device
7. Grant ACTIVITY_RECOGNITION permission when prompted
8. Shake device to test

## Technical Details

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Sensor**: TYPE_ACCELEROMETER
- **Detection**: Acceleration threshold (12.0) + 500ms cooldown
- **Permissions**: ACTIVITY_RECOGNITION (Android 10+)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)