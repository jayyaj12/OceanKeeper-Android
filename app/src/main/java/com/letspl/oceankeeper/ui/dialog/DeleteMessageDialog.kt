package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogDeleteMessageBinding

class DeleteMessageDialog(context: Context, private val onClickLogout: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogDeleteMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDeleteMessageBinding.inflate(layoutInflater)
        binding.deleteMessageDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedDelete() {
        onClickLogout()
        dismiss()
    }

}