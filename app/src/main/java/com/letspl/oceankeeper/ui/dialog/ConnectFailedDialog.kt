package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogFailedNotificationAlarmBinding

class ConnectFailedDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogFailedNotificationAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFailedNotificationAlarmBinding.inflate(layoutInflater)
        binding.connectFailedDialog = this
        setCancelable(false)
        setContentView(binding.root)
    }

    fun onClickCheck() {
        dismiss()
    }

}