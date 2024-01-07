package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogDeleteMessageBinding
import com.letspl.oceankepper.databinding.DialogLogoutBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class DeleteMessageDialog(context: Context, private val onClickLogout: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogDeleteMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDeleteMessageBinding.inflate(layoutInflater)
        binding.deleteMessageDialog = this
        setContentView(binding.root)
    }

    fun onClickClose() {
        dismiss()
    }

    fun onClickedDelete() {
        onClickLogout()
        dismiss()
    }

}