package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankeeper.databinding.DialogGarbageCategoryChoiceBinding
import com.letspl.oceankeeper.ui.viewmodel.MainViewModel

class GarbageCategoryChoiceDialog(context: Context, private val mainViewModel: MainViewModel, private val lifecycleOwner: LifecycleOwner, private val onClickedSaveBtn: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogGarbageCategoryChoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogGarbageCategoryChoiceBinding.inflate(layoutInflater)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = lifecycleOwner
        setContentView(binding.root)
        setCancelable(false)

        binding.closeBtn.setOnClickListener {
            mainViewModel.closeGarbageCategoryModal()
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            onClickedSaveBtn()
            dismiss()
        }
    }

}