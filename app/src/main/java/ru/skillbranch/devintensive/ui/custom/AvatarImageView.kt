package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import ru.skillbranch.devintensive.R

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
: CircleImageView(context, attrs, defStyleAttr) {
    private lateinit var paint: Paint
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas

    fun setInitials(initials: String) {
        paint = Paint().apply {
            isAntiAlias = true
            this.textSize = layoutParams.height/2.33f
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())
        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()

        bitmap = Bitmap.createBitmap(layoutParams.width, layoutParams.height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(getColorFromInitials(initials))
        canvas = Canvas(bitmap)
        canvas.drawText(initials, backgroundBounds.centerX(), textBottom , paint)
        setImageBitmap(bitmap)
        invalidate()
    }

    private fun getColorFromInitials(initials: String) : Int {
        return when(initials.hashCode()%10) {
            0 -> resources.getColor(R.color.color_avatar1, context.theme)
            1 -> resources.getColor(R.color.color_avatar2, context.theme)
            2 -> resources.getColor(R.color.color_avatar3, context.theme)
            3 -> resources.getColor(R.color.color_avatar4, context.theme)
            4 -> resources.getColor(R.color.color_avatar5, context.theme)
            5 -> resources.getColor(R.color.color_avatar6, context.theme)
            6 -> resources.getColor(R.color.color_avatar7, context.theme)
            7 -> resources.getColor(R.color.color_avatar8, context.theme)
            8 -> resources.getColor(R.color.color_avatar9, context.theme)
            else -> resources.getColor(R.color.color_avatar10, context.theme)
        }
    }
}