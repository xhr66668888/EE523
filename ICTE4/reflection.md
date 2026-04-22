# ICTE4 - Reflection

## App Feature
Our app uses the **accelerometer sensor** to detect device shakes and count them. It displays real-time acceleration values (X, Y, Z) and a shake counter.

## How Gemini Helped
Gemini provided a starting point for:
1. Sensor access code structure
2. Basic Compose UI layout
3. Permission requirements (ACTIVITY_RECOGNITION)

## Errors and Weaknesses Found
1. **Missing Permissions**: Gemini didn't include runtime permission handling
2. **No Shake Detection Logic**: Gemini's code only showed raw sensor data, not shake detection
3. **No Debouncing**: Without cooldown logic, multiple shakes would be counted for a single motion
4. **Lifecycle Issues**: Basic lifecycle handling was incomplete

## Changes We Made
1. Added complete runtime permission request flow
2. Implemented shake detection algorithm with threshold and cooldown
3. Added proper lifecycle management with DisposableEffect
4. Created a more polished UI with card layout and better typography

## Lessons Learned
1. AI is a helpful starting point but requires human refinement
2. Testing on real devices is essential for sensor-based apps
3. Permission handling is often overlooked by AI
4. Debouncing is critical for motion-based interactions