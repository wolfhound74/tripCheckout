package ru.zavbus.zavbusexample.utils

import android.content.Context
import android.view.View
import android.widget.Toast


class ToastMessage {
    fun init(context: Context, message: String, success: Boolean = true) {
        val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        val view: View = toast.view

        val color = if (success) ru.zavbus.zavbusexample.R.color.customGreen else ru.zavbus.zavbusexample.R.color.customRed
        view.setBackgroundResource(color)
        toast.show();
    }
}