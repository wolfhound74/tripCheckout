package ru.zavbus.zavbusexample.utils

import android.content.Context
import android.support.v7.app.AlertDialog
import ru.zavbus.zavbusexample.R

class CustomModal {
    fun initSubmitDialog(context: Context, title: String, message: String, func: () -> Unit) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning_yellow_24dp)
                .setPositiveButton(android.R.string.yes, { dialog, whichButton -> func() })
                .setNegativeButton(android.R.string.no, null).show()
    }

    fun initInfoDialog(context: Context, message: String, title: String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, { dialog, whichButton -> })
                .show()
    }
}