package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val view = currentFocus
    if( view != null) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity.isKeyboardOpen(): Boolean {
    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)
    val screenHeight = window.decorView.rootView.height
    val difference = screenHeight - (rect.bottom - rect.top)
    return difference > 200
}

fun Activity.isKeyboardClosed(): Boolean {
    return !isKeyboardOpen()
}