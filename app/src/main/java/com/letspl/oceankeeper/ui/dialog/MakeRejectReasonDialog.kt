package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogMakeRejectReasonBinding

class MakeRejectReasonDialog(context: Context, private val nickname: String, private val onClickReject: (String) -> Unit): Dialog(context) {
    private lateinit var binding: DialogMakeRejectReasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogMakeRejectReasonBinding.inflate(layoutInflater)
        binding.makeRejectReasonDialog = this
        setContentView(binding.root)

        binding.topTv.text = "${nickname}님의 활동 신청을 거절하시겠어요?"
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedReject() {
        onClickReject(binding.rejectReasonEt.text.toString())
        dismiss()
    }

}