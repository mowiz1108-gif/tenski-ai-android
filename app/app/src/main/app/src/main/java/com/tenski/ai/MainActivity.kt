
package com.tenski.ai

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlin.math.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 5, 15)
        window.navigationBarColor = Color.rgb(5, 5, 15)
        setContentView(TenskiView())
    }
}

class TenskiView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var phase = 0f

    private val animator = ValueAnimator.ofFloat(0f, 2f * PI.toFloat())

    constructor(context: android.content.Context) : super(context) {
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
        val top = 24f * density
        val w = min(width * 0.78f, 300f * density)
        val h = 66f * density

        val rect = RectF(
            cx - w / 2,
            top,
            cx + w / 2,
            top + h
        )

        // Outer glow
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f * density
        paint.shader = LinearGradient(
            rect.left, top, rect.right, top + h,
            intArrayOf(
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA
            ),
            null,
            Shader.TileMode.CLAMP
        )
        paint.setShadowLayer(
            22f * density,
            0f, 0f,
            Color.rgb(80, 60, 255)
        )
        canvas.drawRoundRect(rect, h / 2, h / 2, paint)
        paint.clearShadowLayer()

        // Dark island interior
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
            val barHeight = (7f + wave * 22f) * density
            val x = startX + i * gap

            paint.color = Color.rgb(
                70 + (i * 8).coerceAtMost(100),
                170,
                255
            )

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

        paint.color = Color.LTGRAY
        paint.textSize = 11f * density

        canvas.drawText(
            "Your AI assistant",
            cx,
            top + h + 58f * density,
            paint
        )
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }
}
