# Reflection - HW2

## What features or sensors did your app use?

My app uses 3 tabs. The sensors tab uses Light sensor, Proximity sensor, and Accelerometer. The draw tab lets you select an image from gallery and draw on it with different colors and save it. The camera tab uses CameraX to take photos with front or back camera.

## How did the LLM help you?

The LLM helped me a lot with this assignment. It helped me set up the bottom navigation which I had no idea how to do. It also explained how sensors work in Android and how to register listeners. For the drawing part, it helped me understand how touch coordinates work and how to map them to bitmap coordinates. For camera, it helped me figure out CameraX and the permission issues.

## What errors, weaknesses, or missing pieces did you find in the LLM's output?

- Sometimes the LLM gave me code that didn't compile because of wrong import statements or wrong function names
- For the drawing view coordinate mapping, the LLM's first attempt was wrong and I had to ask follow up questions to get it right
- The LLM didn't always mention version-specific things like the permission changes in Android 13, I had to ask about crashes to find out about that
- Some code the LLM gave was too "clean" and looked like it was from a textbook, I had to simplify it to make it look like my own code

## What did you change or fix?

- Fixed the EXIF rotation handling because the LLM's initial code only handled some orientations
- Changed the permission handling to account for different Android SDK versions
- Simplified some of the drawing view code because the LLM made it more complex than it needed to be
- Added null checks for sensors because not all devices have all sensors

## What did you learn about using AI in software development?

I learned that AI is really helpful for getting started with things you don't know how to do, like CameraX setup. But you can't just copy paste the code and expect it to work. You need to understand what the code does so you can fix bugs and customize it. Also sometimes the AI gives wrong answers so you need to verify things yourself. It's like having a tutor that explains things but sometimes makes mistakes. It's a good tool but you still need to learn the underlying concepts.
