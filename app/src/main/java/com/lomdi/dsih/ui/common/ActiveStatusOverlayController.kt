package com.lomdi.dsih.ui.common

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Controller to show/hide a persistent "Anti-Lomdi Active" badge.
 */
object ActiveStatusOverlayController {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    fun show(context: Context) {
        handler.post {
            if (overlayView != null) return@post

            if (!Settings.canDrawOverlays(context)) {
                Log.w("ActiveStatusOverlay", "Permission missing. Launching settings.")
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return@post
            }

            try {
                windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val density = context.resources.displayMetrics.density

                // 1. Create Pill Badge
                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding((12 * density).toInt(), (6 * density).toInt(), (12 * density).toInt(), (6 * density).toInt())
                    
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 100f * density
                        setColor(Color.parseColor("#E0E0E0")) // Light Gray
                        setStroke((1 * density).toInt(), Color.parseColor("#4CAF50")) // Green border
                    }
                }

                // 2. Green Pulse Dot
                val dot = View(context).apply {
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor("#4CAF50")) // Material Green
                    }
                    val size = (8 * density).toInt()
                    layoutParams = LinearLayout.LayoutParams(size, size).apply {
                        marginEnd = (8 * density).toInt()
                    }
                }
                layout.addView(dot)

                // 3. Status Text
                val text = TextView(context).apply {
                    text = "Anti-Lomdi Active"
                    setTextColor(Color.parseColor("#2E7D32")) // Dark Green
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                }
                layout.addView(text)

                // 4. Window Parameters
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.END
                    y = (100 * density).toInt() // Positioned cleanly below status bar
                    x = (16 * density).toInt()
                }

                windowManager?.addView(layout, params)
                overlayView = layout
                
            } catch (e: Exception) {
                Log.e("ActiveStatusOverlay", "Error injecting status badge.", e)
            }
        }
    }

    fun hide() {
        handler.post {
            try {
                overlayView?.let {
                    windowManager?.removeView(it)
                    overlayView = null
                }
            } catch (e: Exception) {
                Log.e("ActiveStatusOverlay", "Error removing status badge.", e)
            }
        }
    }

    fun isVisible(): Boolean = overlayView != null
}
