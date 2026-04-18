# HW2 - SwapSense App

## How the App Works

This app has 3 tabs at the bottom:

1. **Sensors Tab**: Shows data from 3 sensors on the phone - Light sensor, Proximity sensor, and Accelerometer. The values update in real time. If a sensor is not available on the device it shows a Toast message.

2. **Draw Tab**: You can pick an image from your photo gallery and draw on top of it with different brush colors (black, red, blue). There's buttons to save the edited image as a new photo in the gallery, and to reset back to the original photo.

3. **Camera Tab**: Uses CameraX to take photos. You can switch between front and back camera with the Switch button. When you capture a photo it saves it to the gallery and also shows it in the ImageView below the preview.

## Hours Spent

It took me about 14 hours to complete this assignment. A lot of that time was spent debugging and figuring out how CameraX works and how to handle the image drawing coordinates.

## Most Challenging Parts

- The DrawingImageView was really hard. Getting the touch coordinates to map correctly to the bitmap coordinates was confusing. I had to calculate the scale and offset manually and I'm still not 100% sure it works perfectly on all screen sizes.
- CameraX permissions were tricky because different Android versions need different permissions. I kept getting crashes on Android 13 because I was requesting WRITE_EXTERNAL_STORAGE which is not needed anymore.
- The EXIF orientation for the draw tab - some photos from the gallery come in rotated and I had to figure out how to read the EXIF data and rotate the bitmap.
i also encountered a fatal issue when running the app on my phone, and used claude for helping me debug and solve that 16kb issue see 16kb md for details from claude

## Resources Used

1. Android Developer Docs - CameraX: https://developer.android.com/training/camerax
2. Android Developer Docs - Sensors: https://developer.android.com/guide/topics/sensors
3. Android Developer Docs - MediaStore: https://developer.android.com/training/data-storage/shared/media
4. Stack Overflow - How to get EXIF orientation and rotate image
5. Stack Overflow - CameraX permission handling for different SDK versions
6. SVG Repo for icons: https://www.svgrepo.com (used for the brush icon)
7. Class slides for sensor implementation
