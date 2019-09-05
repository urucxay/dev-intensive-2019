package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dp
import ru.skillbranch.devintensive.extensions.toDp
import kotlin.math.min

open class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private val paintCircle: Paint = Paint().apply { isAntiAlias = true }
    private val paintBitmap: Paint = Paint().apply { isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)}
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }

    private var borderWidth = DEFAULT_BORDER_WIDTH.dp
    private var borderColor = DEFAULT_BORDER_COLOR

    private var civImage: Bitmap? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
        borderWidth = attributes.getDimension(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
        borderColor = attributes.getColor(R.styleable.CircleImageView_cv_borderColor, borderColor)
        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        civImage = drawableToBitmap(drawable) ?: return
        val radius = min(width, height)/2f

        setLayerType(LAYER_TYPE_HARDWARE, paintCircle)
        canvas.drawCircle(radius,radius,radius, paintCircle)
        canvas.drawBitmap(civImage!!, 0f, 0f, paintBitmap)

        if (borderWidth > 0) {
            paintBorder.color = borderColor
            paintBorder.strokeWidth = borderWidth
            paintBorder.style = Paint.Style.STROKE
            canvas.drawCircle(radius, radius, radius-borderWidth/2, paintBorder)
        }
    }

    fun getBorderWidth(): Int = borderWidth.toInt().toDp()

    fun setBorderWidth(widthInDp: Int) {
        borderWidth = widthInDp.toFloat().dp
        invalidate()
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(context, colorId)
        invalidate()
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? = when (drawable) {
        null -> null
        is BitmapDrawable -> drawable.bitmap
        else -> {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}