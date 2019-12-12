package ru.zavbus.zavbusexample.utils

import android.content.Context
import android.support.v7.app.AlertDialog

class CustomModal {
    fun initSubmitDialog(context: Context, title: String, message: String, func: () -> Unit) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, { dialog, whichButton -> func() })
                .setNegativeButton(android.R.string.no, null).show()
    }
}