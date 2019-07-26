package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt

class CircleImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private val paint: Paint = Paint().apply { isAntiAlias = true }
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }

    private var circleCenter = 0f
    private var heightCircle = 0

    private var borderWidth = DEFAULT_BORDER_WIDTH * resources.displayMetrics.density
    private var borderColor = DEFAULT_BORDER_COLOR

    private var civImage: Bitmap? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
        borderWidth = attributes.getDimension(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
        borderColor = attributes.getColor(R.styleable.CircleImageView_cv_borderColor, borderColor)
        attributes.recycle()
    }

    override fun getScaleType(): ScaleType =
            super.getScaleType().let { if (it == null || it != CENTER_INSIDE) CENTER_CROP else it }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != CENTER_CROP && scaleType != CENTER_INSIDE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported. " + "Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.", scaleType))
        } else {
            super.setScaleType(scaleType)
        }
    }

    override fun onDraw(canvas: Canvas) {
        loadBitmap()

        if (civImage == null) return

        val circleCenterWithBorder = circleCenter + borderWidth

        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenterWithBorder, paintBorder)
        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenter, paint)
    }

    fun getBorderWidth(): Int = borderWidth.roundToInt()

    fun setBorderWidth(dp: Int) {
        borderWidth = dp * resources.displayMetrics.density
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(context, colorId)
    }

    private fun update() {
        if (civImage != null)
            updateShader()

        val usableWidth = width - (paddingLeft + paddingRight)
        val usableHeight = height - (paddingTop + paddingBottom)

        heightCircle = min(usableWidth, usableHeight)

        circleCenter = (heightCircle - borderWidth * 2) / 2
        paintBorder.color = borderColor

        invalidate()
    }

    private fun loadBitmap() {
        civImage = drawableToBitmap(drawable)
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    private fun updateShader() {
        civImage?.also {
            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            val scale: Float
            val dx: Float
            val dy: Float

            when (scaleType) {
                CENTER_CROP -> if (it.width * height > width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                CENTER_INSIDE -> if (it.width * height < width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                else -> {
                    scale = 0f
                    dx = 0f
                    dy = 0f
                }
            }

            shader.setLocalMatrix(Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            })

            paint.shader = shader
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
        when (drawable) {
            null -> null
            is BitmapDrawable -> drawable.bitmap
            else -> try {
                // Create Bitmap object out of the drawable
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}