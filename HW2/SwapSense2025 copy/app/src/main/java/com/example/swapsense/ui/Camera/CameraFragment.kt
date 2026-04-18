package com.example.swapsense.ui.Camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.Manifest
import com.example.swapsense.R
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var imageCapture: ImageCapture? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var previewView: PreviewView
    private lateinit var imageView: ImageView
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val requiredPermissions: Array<String>
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // android 13 and above
                arrayOf(Manifest.permission.CAMERA)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // android 10 to 12
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                // older androids
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            for ((key, value) in permissions) {
                if (!value) {
                    allGranted = false
                    println("permission denied: " + key)
                }
            }
            if (allGranted) {
                startCamera()
                println("all permissions granted")
            } else {
                Toast.makeText(requireContext(), "Camera permissions are required", Toast.LENGTH_LONG).show()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        previewView = view.findViewById(R.id.previewView)
        imageView = view.findViewById(R.id.imageView_photo)

        var captureBtn = view.findViewById<Button>(R.id.btn_capture)
        var switchBtn = view.findViewById<Button>(R.id.btn_switch_camera)

        captureBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                takePhoto()
            }
        })

        switchBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // toggle between front and back
                if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                    lensFacing = CameraSelector.LENS_FACING_FRONT
                } else {
                    lensFacing = CameraSelector.LENS_FACING_BACK
                }
                startCamera() // restart camera with new lens
                println("switched camera to " + if (lensFacing == CameraSelector.LENS_FACING_FRONT) "front" else "back")
            }
        })

        // check permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }

    }

    private fun allPermissionsGranted(): Boolean {
        for (perm in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            var cameraProvider = cameraProviderFuture.get()

            // set up preview
            var preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // set up image capture
            imageCapture = ImageCapture.Builder().build()

            // select front or back camera
            var cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            try {
                // unbind everything first
                cameraProvider.unbindAll()

                // bind use cases to lifecycle
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                println("camera started ok")

            } catch (e: Exception) {
                Log.e("camera_debug", "Use case binding failed", e)
                println("camera bind failed: " + e.message)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        var capture = imageCapture ?: return

        // create filename with timestamp
        var name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(System.currentTimeMillis())

        var contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SwapSense")
            }
        }

        var outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    var savedUri = output.savedUri
                    println("photo saved: " + savedUri.toString())
                    Toast.makeText(requireContext(), "Photo saved!", Toast.LENGTH_SHORT).show()

                    // display the photo in imageview
                    savedUri?.let {
                        imageView.setImageURI(it)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("camera_debug", "Photo capture failed: " + exception.message, exception)
                    Toast.makeText(requireContext(), "Capture failed", Toast.LENGTH_SHORT).show()
                    println("capture error: " + exception.message)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
