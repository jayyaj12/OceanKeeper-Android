package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogSendInquireMessageBinding

class InquireMessageDialog(context: Context, private val nickname: String, private val onClickCheck: (String) -> Unit): Dialog(context) {
    private lateinit var binding: DialogSendInquireMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSendInquireMessageBinding.inflate(layoutInflater)
        binding.inquireMessageDialog = this
        setContentView(binding.root)

        binding.sendMessageTv.text = "${nickname}님의 활동에 대해 문의가 있으신가요?"
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedCheck() {
        onClickCheck(binding.contentsTv.text.toString().trim())
        dismiss()
    }

}