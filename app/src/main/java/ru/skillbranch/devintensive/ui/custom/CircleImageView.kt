package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import ru.skillbranch.devintensive.R
import kotlin.math.min
import android.graphics.BitmapShader
import android.widget.ImageView


class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private lateinit var mBitmapShader: Shader
    private var mShaderMatrix: Matrix

    private var mBitmapDrawBounds: RectF
    private var mBorderBounds: RectF

    private var mBitmap: Bitmap? = null

    private var mBitmapPaint: Paint
    private var mBorderPaint: Paint


    var borderWidth = DEFAULT_BORDER_WIDTH
    var borderColor = DEFAULT_BORDER_COLOR


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, defaultBorderSize)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            a.recycle()
        }

        mShaderMatrix = Matrix()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderBounds = RectF()
        mBitmapDrawBounds = RectF()
        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = borderWidth

        setupBitmap()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? =  when(drawable) {
            null -> null
            is BitmapDrawable -> drawable.bitmap
            else -> {val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bitmap
            }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mBorderPaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mBorderBounds.set(mBitmapDrawBounds)
        mBorderBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()

        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        mBitmap = getBitmapFromDrawable(drawable) ?: return

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height
            dx = mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader.setLocalMatrix(mShaderMatrix)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawOval(mBorderBounds, mBorderPaint)
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    fun getBorderWidth(): Int = borderWidth.toInt()

    fun setBorderWidth(dp: Int) {
        borderWidth = dp.toFloat()
    }

    fun getBorderColor(): Color = Color()

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
    }
}
