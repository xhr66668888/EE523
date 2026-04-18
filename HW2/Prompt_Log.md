# Prompt Log - HW2

This file contains the prompts I used with an LLM agent to help complete HW2. I used an LLM assistant instead of Gemini for this assignment.

---

## Conversation Summary

I asked the LLM to help me implement the SwapSense Android app with 3 tabs. The main things I needed help with were:
- Setting up the bottom navigation properly
- Implementing sensor listeners
- Getting the drawing view to work with touch coordinates
- CameraX setup and permissions

---

## Prompt 1

**Prompt:** "how do i set up bottom navigation with 3 tabs in android kotlin. i have the template already with bottom_nav_menu.xml but i don't know how to connect it to the fragments"

**LLM Response:** The LLM explained I need to set up a NavHostFragment in the activity_main.xml and use NavigationUI.setupWithNavController() in MainActivity. It also told me to create a mobile_navigation.xml file with the 3 fragments defined.

**What happened:** This worked mostly but I had to fix the navigation graph IDs to match the menu item IDs which the LLM didn't mention at first.

---

## Prompt 2

**Prompt:** "im trying to use sensor manager to get light and accelerometer data but the values are not updating on screen. here is my code: [code snippet]. what am i doing wrong"

**LLM Response:** The LLM pointed out that I forgot to register the SensorEventListener with the sensor manager. I was creating the listener but never calling registerListener(). Also told me to unregister in onDestroyView to save battery.

**What happened:** After adding the registerListener call it worked. The LLM also suggested adding null checks for sensors which was helpful because my phone doesn't have all sensors.

---

## Prompt 3

**Prompt:** "for the drawing tab i need to let users draw on an image. how do i make a custom view that handles touch events and draws paths on a bitmap"

**LLM Response:** The LLM helped me create the DrawingImageView class extending AppCompatImageView. It showed how to override onTouchEvent to capture touch coordinates and use Path and Canvas to draw. Also explained I need to convert screen coordinates to bitmap coordinates using scale and offset.

**What happened:** The basic drawing worked but the coordinates were off. I had to ask a follow up question about the coordinate conversion because the LLM's initial calculation didn't account for the image being centered in the view. After some back and forth I got it working.

---

## Prompt 4

**Prompt:** "how do i save an image to the gallery in android. i have a bitmap and want to save it as a new file"

**LLM Response:** The LLM showed me how to use MediaStore to insert a new image. It gave me the ContentValues setup and explained I need to handle IS_PENDING for Android 10+.

**What happened:** This worked pretty well. I had to add the IS_PENDING part because without it the image would show up corrupted in the gallery.

---

## Prompt 5

**Prompt:** "CameraX keeps crashing when i try to switch between front and back camera. also permissions are not working on android 13"

**LLM Response:** The LLM explained that for API 33+ you don't need READ/WRITE_EXTERNAL_STORAGE anymore. It helped me add version checking for the required permissions. For the camera switching issue, it said I need to unbind all use cases before rebinding with the new camera selector.

**What happened:** The unbindAll() fix worked for the camera switching. The permission version check also fixed the Android 13 crash.

---

## Summary

I used the LLM for most of the implementation help. It was really useful for explaining concepts like how CameraX lifecycle binding works and how to handle the drawing canvas coordinates. Sometimes the LLM gave code that didn't compile on the first try (like missing imports or wrong parameter names) so I had to fix those myself. Overall it saved me a lot of time reading documentation but I still needed to understand what the code was doing to debug issues.
