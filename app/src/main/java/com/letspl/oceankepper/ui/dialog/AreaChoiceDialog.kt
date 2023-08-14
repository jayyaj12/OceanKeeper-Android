package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class AreaChoiceDialog(context: Context, private val mainViewModel: MainViewModel, private val lifecycleOwner: LifecycleOwner, private val onClickedSaveBtn: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogAreaChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAreaChoiceBinding.inflate(layoutInflater)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = lifecycleOwner
        setContentView(binding.root)

        binding.closeBtn.setOnClickListener {
            mainViewModel.closeAreaModal()
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            onClickedSaveBtn()
            dismiss()
        }
    }

}