package ru.skillbranch.devintensive.extensions

import android.content.res.Resources

fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Float.toDp() : Float = (this / Resources.getSystem().displayMetrics.density + 0.5f)


val Int.dp
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp
    get() = this * Resources.getSystem().displayMetrics.density
