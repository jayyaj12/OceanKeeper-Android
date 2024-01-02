package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogListDeleteBinding
import com.letspl.oceankepper.databinding.DialogMakeRejectReasonBinding
import com.letspl.oceankepper.databinding.DialogNoShowCheckBinding
import com.letspl.oceankepper.databinding.DialogRejectReasonBinding

class NoShowCheckDialog(context: Context, private val onClickNoShow: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogNoShowCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoShowCheckBinding.inflate(layoutInflater)
        binding.noShowCheckDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedNoShow() {
        onClickNoShow()
        dismiss()
    }

}