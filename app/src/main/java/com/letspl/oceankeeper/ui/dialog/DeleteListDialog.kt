package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogListDeleteBinding

class DeleteListDialog(context: Context, private val onClickDelete: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogListDeleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListDeleteBinding.inflate(layoutInflater)
        binding.deleteListDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedDelete() {
        onClickDelete()
        dismiss()
    }

}