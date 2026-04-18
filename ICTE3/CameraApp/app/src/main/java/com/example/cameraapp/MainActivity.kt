package com.example.cameraapp

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Week 2 flow (see Week2-WEB): start the system camera with an Intent
 * (ACTION_IMAGE_CAPTURE + EXTRA_OUTPUT), request CAMERA at runtime
 * (ContextCompat.checkSelfPermission), return the image to this activity.
 *
 * Sample-Code alignment:
 * - AwesomeCamera / tutorial: take picture, show in ImageView, clear preview.
 * - react-native-image-picker example App.tsx: "Take Image" and "Select Image".
 */
class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnTakePhoto: MaterialButton
    private lateinit var btnPickImage: MaterialButton
    private lateinit var btnDeleteImage: MaterialButton
    private lateinit var btnSavePhoto: MaterialButton

    private var cameraPhotoUri: Uri? = null
    private var previewBitmap: Bitmap? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Toast.makeText(this, getString(R.string.msg_capture_cancelled), Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        cameraPhotoUri?.let { uri ->
            showImageFromUri(uri)
            Toast.makeText(this, getString(R.string.msg_photo_captured), Toast.LENGTH_SHORT).show()
        }
    }

    private val pickFromGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(this, getString(R.string.msg_pick_cancelled), Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        showImageFromUri(uri)
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openCameraWithIntent()
        } else {
            Toast.makeText(this, getString(R.string.msg_need_camera), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnDeleteImage = findViewById(R.id.btnDeleteImage)
        btnSavePhoto = findViewById(R.id.btnSavePhoto)

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                openCameraWithIntent()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        btnPickImage.setOnClickListener {
            pickFromGalleryLauncher.launch("image/*")
        }

        btnDeleteImage.setOnClickListener {
            imageView.setImageResource(R.drawable.ic_placeholder)
            cameraPhotoUri = null
            previewBitmap = null
            Toast.makeText(this, getString(R.string.msg_image_deleted), Toast.LENGTH_SHORT).show()
        }

        btnSavePhoto.setOnClickListener {
            saveToPhotoLibrary()
        }
    }

    private fun openCameraWithIntent() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        cameraPhotoUri = photoUri

        // Same pattern as Android docs / Week 2: delegate capture to the camera app.
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(intent)
        } else {
            Toast.makeText(this, getString(R.string.msg_no_camera_app), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun showImageFromUri(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { input ->
            val bitmap = BitmapFactory.decodeStream(input)
            previewBitmap = bitmap
            imageView.setImageBitmap(bitmap)
        }
    }

    /** Persists the current preview into the shared MediaStore (photo library). */
    private fun saveToPhotoLibrary() {
        val bitmap = previewBitmap ?: run {
            Toast.makeText(this, getString(R.string.msg_no_photo), Toast.LENGTH_SHORT).show()
            return
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "CameraApp_$timeStamp.jpg"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri == null) {
            Toast.makeText(this, getString(R.string.msg_save_failed), Toast.LENGTH_SHORT).show()
            return
        }

        contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Toast.makeText(this, getString(R.string.msg_saved_photo_library), Toast.LENGTH_SHORT).show()
    }
}
