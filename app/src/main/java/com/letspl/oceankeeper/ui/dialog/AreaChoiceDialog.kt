package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankeeper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankeeper.ui.viewmodel.MainViewModel

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