package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ApplyCancelDialog(context: Context, private val onClickCancel: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogCancelApplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCancelApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelBtn.setOnClickListener {
            onClickCancel()
            dismiss()
        }
    }

}