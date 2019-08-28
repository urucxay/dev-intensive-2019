package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dp
import ru.skillbranch.devintensive.utils.Utils
import kotlin.random.Random

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
: CircleImageView(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_INITIALS = "??"
    }

    private var initials: String = DEFAULT_INITIALS
    private lateinit var paint: Paint
    private lateinit var bitmap: Bitmap

//    init {
//        if (attrs != null) {
//            val attributes = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyleAttr, 0)
//            initials = attributes.getString(R.styleable.AvatarImageView_initials) ?: ""
//            attributes.recycle()
//        }
//    }

    override fun onDraw(canvas: Canvas) {
//        if (this.drawable == null) {
//            drawDefaultAvatar1(initials)
//        }

//        paint = Paint().apply {
//            isAntiAlias = true
//            this.textSize = (height/2.5f).dp
//            color = Color.BLACK
//            textAlign = Paint.Align.CENTER
//        }
//        val textBounds = Rect()
//        paint.getTextBounds(initials, 0, initials.length, textBounds)
//        val backgroundBounds = RectF()
//        backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())
//        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
//
//        canvas.drawColor(Utils.getThemeAccentColor(context))
//        canvas.drawText(initials, backgroundBounds.centerX(), textBottom , paint)
        super.onDraw(canvas)

    }

    fun setInitials(initials: String) {
        setImageBitmap(drawDefaultAvatar1(initials))
        invalidate()
    }

    private fun drawDefaultAvatar1(initials: String, textSize: Float = layoutParams.height/2.33f, textColor: Int = Color.WHITE) : Bitmap {
        paint = Paint().apply {
            isAntiAlias = true
            this.textSize = textSize
            color = textColor
            textAlign = Paint.Align.CENTER
        }

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())
        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()

        bitmap = Bitmap.createBitmap(layoutParams.width, layoutParams.height, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Utils.getThemeAccentColor(context))
        bitmap.eraseColor(getRandomColor())
        val canvas = Canvas(bitmap)
        canvas.drawText(initials, backgroundBounds.centerX(), textBottom , paint)
        return bitmap
    }

    private fun getRandomColor() : Int {
        return when(Random.nextInt(5)) {
            0 -> R.color.color_primary
            1 -> R.color.color_accent_night
            2 -> Color.RED
            3 -> Color.MAGENTA
            else ->Color.BLUE
        }
    }
}