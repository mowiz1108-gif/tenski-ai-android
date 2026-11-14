
package com.tenski.ai

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.View
import android.view.MotionEvent
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import kotlin.math.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(5, 5, 15)
        window.navigationBarColor = Color.rgb(5, 5, 15)

        setContentView(TenskiView(this))
    }
}

class TenskiView(context: android.content.Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var phase = 0f

    private val modes = arrayOf(
        "IDLE",
        "LISTENING",
        "THINKING",
        "SPEAKING"
    )

    private var currentMode = 0

    private val animator = ValueAnimator.ofFloat(
        0f, 2f * PI.toFloat()
    )

    init {
        animator.duration = 2400
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener {
            phase = it.animatedValue as Float
            invalidate()
        }

        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.rgb(5, 5, 15))

        val density = resources.displayMetrics.density
        val cx = width / 2f
        val top = 12f * density
        val w = min(width * 0.78f, 300f * density)
        val h = 66f * density

        val rect = RectF(
            cx - w / 2,
            top,
            cx + w / 2,
            top + h
        )

        val colors = when (currentMode) {
            1 -> intArrayOf(
                Color.CYAN, Color.BLUE, Color.CYAN
            )
            2 -> intArrayOf(
                Color.MAGENTA, Color.BLUE, Color.MAGENTA
            )
            3 -> intArrayOf(
                Color.rgb(0, 255, 180),
                Color.CYAN,
                Color.MAGENTA
            )
            else -> intArrayOf(
                Color.CYAN, Color.BLUE, Color.MAGENTA
            )
        }

        // Island outline and glow
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f * density
        paint.shader = LinearGradient(
            rect.left, top, rect.right, top + h,
            colors, null, Shader.TileMode.CLAMP
        )

        paint.setShadowLayer(
            if (currentMode == 0) 15f * density else 25f * density,
            0f, 0f, colors[1]
        )

        canvas.drawRoundRect(rect, h / 2, h / 2, paint)
        paint.clearShadowLayer()

        // Island interior
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            rect.left, top, rect.right, top + h,
            Color.rgb(15, 18, 38),
            Color.rgb(28, 12, 40),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(rect, h / 2, h / 2, paint)
        paint.shader = null

        // Animated waveform
        val barCount = 17
        val centerY = top + h / 2
        val startX = cx - 48f * density
        val gap = 6f * density

        for (i in 0 until barCount) {

            val wave = abs(
                sin(phase + i * 0.48f)
            )

            val intensity = when (currentMode) {
                0 -> 0.5f
                1 -> 1.0f
                2 -> 0.7f
                else -> 1.2f
            }

            val barHeight =
                (5f + wave * 22f * intensity) * density

            val x = startX + i * gap

            paint.color = colors[i % colors.size]

            canvas.drawRoundRect(
                x,
                centerY - barHeight / 2,
                x + 3f * density,
                centerY + barHeight / 2,
                4f * density,
                4f * density,
                paint
            )
        }

        // Assistant name
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = 13f * density
        paint.typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(
            "TENSKI AI",
            cx,
            top + h + 38f * density,
            paint
        )

        // Current state
        paint.color = Color.LTGRAY
        paint.textSize = 11f * density

        canvas.drawText(
            modes[currentMode],
            cx,
            top + h + 58f * density,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            currentMode = (currentMode + 1) % modes.size

            Toast.makeText(
                context,
                modes[currentMode],
                Toast.LENGTH_SHORT
            ).show()

            invalidate()
            performClick()

            return true
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }
}

