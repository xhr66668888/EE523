package com.example.bottomnavdrawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private var drawCanvas: Canvas? = null
    private var bitmap: Bitmap? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(bitmap!!)
        drawCanvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { bmp ->
            canvas.drawBitmap(bmp, 0f, 0f, null)
        }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                drawCanvas?.drawPath(path, paint)
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)
                drawCanvas?.drawPath(path, paint)
                path.reset()
            }
        }

        invalidate()
        return true
    }

    fun clearCanvas() {
        drawCanvas?.drawColor(Color.WHITE)
        invalidate()
    }
}