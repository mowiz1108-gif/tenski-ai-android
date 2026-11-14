
package com.tenski.ai

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.View
import android.widget.TextView

class TenskiOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var island: TextView? = null
    private var expanded = false

    private val channelId = "tenski_overlay"

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, createNotification())

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        showIsland()
    }

    private fun showIsland() {
        val density = resources.displayMetrics.density

        val pill = TextView(this).apply {
            text = "●  TENSKI AI"
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(24, 12, 24, 12)

            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.rgb(0, 220, 255),
                    Color.rgb(55, 70, 255),
                    Color.rgb(180, 30, 230)
                )
            ).apply {
                cornerRadius = 60f * density
            }

            setOnClickListener {
                expanded = !expanded

                text = if (expanded) {
                    "TENSKI AI\nTap to collapse"
                } else {
                    "●  TENSKI AI"
                }

                layoutParams = layoutParams?.apply {
                    width = (if (expanded) 250 else 190) * density.toInt()
                    height = (if (expanded) 90 else 50) * density.toInt()
                }

                requestLayout()
            }
        }

        val params = WindowManager.LayoutParams(
            (190 * density).toInt(),
            (50 * density).toInt(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = (28 * density).toInt()
        }

        island = pill
        windowManager.addView(pill, params)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "TENSKI AI Overlay",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("TENSKI AI is active")
            .setContentText("Floating assistant is running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onDestroy() {
        island?.let {
            if (::windowManager.isInitialized) {
                windowManager.removeView(it)
            }
        }

        island = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
