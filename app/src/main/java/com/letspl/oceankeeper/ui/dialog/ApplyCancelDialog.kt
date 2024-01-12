package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogCancelApplyBinding

class ApplyCancelDialog(context: Context, private val onClickCancel: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogCancelApplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCancelApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelBtn.setOnClickListener {
            onClickCancel()
            dismiss()
        }
    }

}