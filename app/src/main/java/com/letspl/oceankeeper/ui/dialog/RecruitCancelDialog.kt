package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogCancelRecruitBinding

class RecruitCancelDialog(context: Context, private val onClickCancel: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogCancelRecruitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCancelRecruitBinding.inflate(layoutInflater)
        binding.recruitCancelDialog = this
        setContentView(binding.root)
        setCancelable(false)

        binding.cancelBtn.setOnClickListener {
            onClickCancel()
            dismiss()
        }
    }

    fun onClickClose() {
        dismiss()
    }

}