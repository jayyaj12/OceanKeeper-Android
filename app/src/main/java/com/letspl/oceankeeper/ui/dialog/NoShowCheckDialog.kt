package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogNoShowCheckBinding

class NoShowCheckDialog(context: Context, private val onClickNoShow: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogNoShowCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoShowCheckBinding.inflate(layoutInflater)
        binding.noShowCheckDialog = this
        setCancelable(false)
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