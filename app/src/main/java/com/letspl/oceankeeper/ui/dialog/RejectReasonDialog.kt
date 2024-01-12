package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogRejectReasonBinding

class RejectReasonDialog(context: Context, private val reasonText: String): Dialog(context) {
    private lateinit var binding: DialogRejectReasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogRejectReasonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reasonTv.text = reasonText

        binding.confirmBtn.setOnClickListener {
            dismiss()
        }
    }

}