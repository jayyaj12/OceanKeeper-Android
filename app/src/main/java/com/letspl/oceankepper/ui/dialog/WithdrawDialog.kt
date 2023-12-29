package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogLogoutBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.databinding.DialogWithdrawBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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