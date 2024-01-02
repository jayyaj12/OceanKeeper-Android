package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogListDeleteBinding
import com.letspl.oceankepper.databinding.DialogMakeRejectReasonBinding
import com.letspl.oceankepper.databinding.DialogRejectReasonBinding

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