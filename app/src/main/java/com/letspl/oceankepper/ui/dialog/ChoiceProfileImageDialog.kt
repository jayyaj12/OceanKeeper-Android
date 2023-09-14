package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankepper.databinding.DialogChoicePhotoBinding

class ChoiceProfileImageDialog(context: Context, private val onClickTakePhoto: () -> Unit, private val onClickChoicePhoto: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogChoicePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogChoicePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.takePhotoTv.setOnClickListener {
            onClickTakePhoto()
            dismiss()
        }
        binding.choicePhotoTv.setOnClickListener {
            onClickChoicePhoto()
            dismiss()
        }
    }

}