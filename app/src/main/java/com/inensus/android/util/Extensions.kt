package com.inensus.android.util

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Spinner


fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun Spinner.onTouch() {
    this.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            Utils.hideKeyBoard(v)
        }
        false
    }
}