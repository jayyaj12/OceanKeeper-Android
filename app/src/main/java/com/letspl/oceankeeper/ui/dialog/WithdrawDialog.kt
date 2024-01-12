package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogWithdrawBinding

class WithdrawDialog(context: Context, private val onClickWithdraw: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogWithdrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogWithdrawBinding.inflate(layoutInflater)
        binding.withdrawDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedWithdraw() {
        onClickWithdraw()
        dismiss()
    }

}