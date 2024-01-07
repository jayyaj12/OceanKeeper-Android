package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogLogoutBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.databinding.DialogSendInquireMessageBinding
import com.letspl.oceankepper.databinding.DialogSendMessageBinding
import com.letspl.oceankepper.databinding.DialogSendMessageManageCrewBinding
import com.letspl.oceankepper.databinding.DialogSendReplyMessageBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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