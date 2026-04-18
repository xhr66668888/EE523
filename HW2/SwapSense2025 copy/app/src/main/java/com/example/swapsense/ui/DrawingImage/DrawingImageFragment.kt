package com.example.swapsense.ui.DrawingImage

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.swapsense.R
import android.graphics.Color
import java.io.InputStream


class DrawingImageFragment : Fragment() {
    private lateinit var drawingImageView: DrawingImageView
    private var originalBitmap: Bitmap? = null
    private var currentImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            currentImageUri = it

            try {
                var inputStream: InputStream? = requireContext().contentResolver.openInputStream(it)
                var bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // try to get exif data to rotate
                var input2 = requireContext().contentResolver.openInputStream(it)
                if (input2 != null) {
                    var exif = ExifInterface(input2)
                    var orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                    var rotation = 0f
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270f
                    }

                    if (rotation != 0f) {
                        var matrix = Matrix()
                        matrix.postRotate(rotation)
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
                    }
                    input2.close()
                }

                originalBitmap = bmp
                drawingImageView.setImageBitmap(bmp)
                println("image loaded ok")

            } catch (e: Exception) {
                // something went wrong
                println("error loading image: " + e.message)
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // permission launcher for gallery access
    private val galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            selectImageLauncher.launch("image/*")
            println("gallery permission granted")
        } else {
            Toast.makeText(requireContext(), "Gallery permission is needed to select images", Toast.LENGTH_LONG).show()
            println("gallery permission denied")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_draw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawingImageView = view.findViewById(R.id.drawingImageView)

        var selectBtn = view.findViewById<Button>(R.id.btn_select_image)
        var saveBtn = view.findViewById<Button>(R.id.btn_save)
        var resetBtn = view.findViewById<Button>(R.id.btn_reset)
        var blackBtn = view.findViewById<Button>(R.id.btn_color_black)
        var redBtn = view.findViewById<Button>(R.id.btn_color_red)
        var blueBtn = view.findViewById<Button>(R.id.btn_color_blue)

        // select image from gallery
        selectBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // check gallery permission first
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // android 13+ uses READ_MEDIA_IMAGES
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        selectImageLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    // older androids use READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImageLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        })

        // save the edited image
        saveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (drawingImageView.getBitmapWithDrawing() == null) {
                    Toast.makeText(requireContext(), "No image to save", Toast.LENGTH_SHORT).show()
                    return
                }
                saveImageAsNew()
            }
        })

        // reset to original photo
        resetBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (originalBitmap != null) {
                    drawingImageView.setImageBitmap(originalBitmap)
                    Toast.makeText(requireContext(), "Image reset", Toast.LENGTH_SHORT).show()
                    println("reset done")
                } else {
                    Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // set brush color to black
        blackBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                drawingImageView.setBrushColor(Color.BLACK)
                println("color set to black")
            }
        })

        // set brush color to red
        redBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                drawingImageView.setBrushColor(Color.RED)
                println("color set to red")
            }
        })

        // set brush color to blue
        blueBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                drawingImageView.setBrushColor(Color.BLUE)
                println("color set to blue")
            }
        })

    }

    private fun saveImageAsNew() {
        var bmp = drawingImageView.getBitmapWithDrawing()

        var contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        var uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            saveImageToUri(uri, bmp)
        }
    }

    private fun saveImageToUri(uri: Uri, bmp: Bitmap?) {
        try {
            var outputStream = requireContext().contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bmp?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            }

            // if android 10+ need to update IS_PENDING
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                var values = ContentValues()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                requireContext().contentResolver.update(uri, values, null, null)
            }

            Toast.makeText(requireContext(), "Image saved to gallery!", Toast.LENGTH_SHORT).show()
            println("image saved ok")

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            println("save error: " + e.message)
        }
    }
}
