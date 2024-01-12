package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogLogoutBinding

class LogoutDialog(context: Context, private val onClickLogout: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLogoutBinding.inflate(layoutInflater)
        binding.logoutDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedLogout() {
        onClickLogout()
        dismiss()
    }

}