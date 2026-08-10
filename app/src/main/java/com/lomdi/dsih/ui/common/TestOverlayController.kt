package com.lomdi.dsih.ui.common

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView

/**
 * Diagnostic utility to verify WindowManager overlay rendering across all applications.
 * Displays a small, floating circular badge.
 */
object TestOverlayController {

    private var windowManager: WindowManager? = null
    private var testView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    fun showTestBadge(context: Context) {
        handler.post {
            if (testView != null) return@post
            if (!Settings.canDrawOverlays(context)) {
                Log.e("TestOverlay", "Overlay permission missing.")
                return@post
            }

            try {
                windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val density = context.resources.displayMetrics.density
                val size = (70 * density).toInt()

                // 1. Create Circular Badge
                val badge = TextView(context).apply {
                    text = "Active\nThreat"
                    setTextColor(Color.WHITE)
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor("#B71C1C"))
                    }
                }

                // 2. Window Parameters
                val params = WindowManager.LayoutParams(
                    size, size,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    x = (16 * density).toInt()
                }

                windowManager?.addView(badge, params)
                testView = badge
                Log.d("TestOverlay", "Test badge injected successfully.")
                
            } catch (e: Exception) {
                Log.e("TestOverlay", "Failed to inject test badge.", e)
            }
        }
    }

    fun hideTestBadge() {
        handler.post {
            try {
                testView?.let {
                    windowManager?.removeView(it)
                    testView = null
                }
            } catch (e: Exception) {
                Log.e("TestOverlay", "Error removing test badge.", e)
            }
        }
    }

    fun isBadgeVisible(): Boolean = testView != null
}
