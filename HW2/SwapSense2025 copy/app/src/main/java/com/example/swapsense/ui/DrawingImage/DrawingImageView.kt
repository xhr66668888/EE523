package com.example.swapsense.ui.DrawingImage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
class DrawingImageView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // drawing stuff
    private var drawCanvas: Canvas? = null
    private var bitmap: Bitmap? = null
    private var bitmapScale = 1f
    private var offsetX = 0f
    private var offsetY = 0f

    init {
        setOnTouchListener { _, event ->
            // convert screen touch to bitmap coords
            var x = (event.x - offsetX) / bitmapScale
            var y = (event.y - offsetY) / bitmapScale

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(x, y)
                    // draw on canvas while moving
                    drawCanvas?.drawPath(path, paint)
                }
                MotionEvent.ACTION_UP -> {
                    path.lineTo(x, y)
                    drawCanvas?.drawPath(path, paint)
                    path.reset()
                }
            }

            invalidate()
            true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { bmp ->
            // compute scale and offset to center image
            bitmapScale = min(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
            offsetX = (width - bmp.width * bitmapScale) / 2f
            offsetY = (height - bmp.height * bitmapScale) / 2f

            canvas.save()
            canvas.translate(offsetX, offsetY)
            canvas.scale(bitmapScale, bitmapScale)

            // draw the bitmap with all the drawing paths on it
            canvas.drawBitmap(bmp, 0f, 0f, null)

            canvas.restore()

        }
    }

    override fun setImageBitmap(bmp: Bitmap?) {
        super.setImageBitmap(bmp)
        bmp?.let {
            // make a mutable copy so we can draw on it
            var mutableBmp = it.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = mutableBmp
            drawCanvas = Canvas(mutableBmp)
        }
        invalidate()
    }

    fun getBitmapWithDrawing(): Bitmap? {
        return bitmap
    }

    fun setBrushColor(color: Int) {
        paint.color = color
        // invalidate() // do we need this?
    }
}
