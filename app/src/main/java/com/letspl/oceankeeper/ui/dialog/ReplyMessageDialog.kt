package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogSendReplyMessageBinding

class ReplyMessageDialog(context: Context, private val onClickCheck: (String) -> Unit): Dialog(context) {
    private lateinit var binding: DialogSendReplyMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSendReplyMessageBinding.inflate(layoutInflater)
        binding.replyMessageDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedCheck() {
        onClickCheck(binding.contentsTv.text.toString().trim())
        dismiss()
    }

}