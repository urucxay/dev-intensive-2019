package ru.skillbranch.devintensive.extensions

import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar

fun Snackbar.setBackgroundColor(@ColorInt color: Int): Snackbar {
    this.view.setBackgroundColor(color)
    return this
}

fun Snackbar.setBackgroundDrawable(@DrawableRes drawable: Int): Snackbar {
    this.view.setBackgroundResource(drawable)
    return this
}

fun Snackbar.setTextColor(@ColorInt textColor: Int): Snackbar {
    this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        .setTextColor(textColor)
    return this
}