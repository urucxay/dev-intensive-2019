package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt


class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private val paint: Paint = Paint().apply { isAntiAlias = true }
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }

    private var circleCenter = 0
    private var heightCircle: Int = 0

    private var borderWidth: Float = DEFAULT_BORDER_WIDTH
    private var borderColor: Int = DEFAULT_BORDER_COLOR


    private var civColorFilter: ColorFilter? = null
        set(value) {
            if (field != value) {
                field = value
                if (field != null) {
                    civDrawable = null // To force re-update shader
                    invalidate()
                }
            }
        }

    private var civImage: Bitmap? = null
    private var civDrawable: Drawable? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

        val defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
        borderWidth = attributes.getDimension(R.styleable.CircleImageView_cv_borderWidth, defaultBorderSize)
        borderColor = attributes.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)

        attributes.recycle()
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        civColorFilter = colorFilter
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
        // Load the bitmap
        loadBitmap()

        // Check if civImage isn't null
        if (civImage == null) return

        val circleCenterWithBorder = circleCenter + borderWidth
        val margeWithShadowRadius = 0f

        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenterWithBorder - margeWithShadowRadius, paintBorder)
        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenter - margeWithShadowRadius, paint)
    }

    private fun update() {
        if (civImage != null)
            updateShader()

        val usableWidth = width - (paddingLeft + paddingRight)
        val usableHeight = height - (paddingTop + paddingBottom)

        heightCircle = min(usableWidth, usableHeight)

        circleCenter = (heightCircle - borderWidth * 2).toInt() / 2
        paintBorder.color = borderColor

        manageElevation()
        invalidate()
    }

    private fun manageElevation() {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setOval(0, 0, heightCircle, heightCircle)
            }
        }
    }

    private fun loadBitmap() {
        if (civDrawable == drawable) return

        civDrawable = drawable
        civImage = drawableToBitmap(civDrawable)
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    private fun updateShader() {
        civImage?.also {
            // Create Shader
            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            // Center Image in Shader
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

            // Set Shader in Paint
            paint.shader = shader

            // Apply colorFilter
            paint.colorFilter = civColorFilter
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measure(widthMeasureSpec)
        val height = measure(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measure(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize // The parent has determined an exact size for the child.
            MeasureSpec.AT_MOST -> specSize // The child can be as large as it wants up to the specified size.
            else -> heightCircle // The parent has not imposed any constraint on the child.
        }
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
}