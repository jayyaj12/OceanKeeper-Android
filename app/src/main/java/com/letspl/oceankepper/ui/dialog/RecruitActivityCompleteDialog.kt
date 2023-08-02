package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding

class RecruitActivityCompleteDialog(context: Context, private val text: String, private val onClickedMyActivity: () -> Unit, private val onClickedCheck: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogRecruitActivitiyCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogRecruitActivitiyCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.infoTextTv.text = text

        binding.checkBtn.setOnClickListener {
            onClickedCheck()
            dismiss()
        }

        binding.checkMyActivityLl.setOnClickListener {
            onClickedMyActivity()
        }
    }

}