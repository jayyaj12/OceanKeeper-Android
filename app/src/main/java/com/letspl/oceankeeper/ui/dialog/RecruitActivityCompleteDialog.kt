package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogRecruitActivitiyCompleteBinding
import timber.log.Timber

class RecruitActivityCompleteDialog(context: Context, private val title: String, private val text: String, private val onClickedMyActivity: () -> Unit, private val onClickedCheck: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogRecruitActivitiyCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogRecruitActivitiyCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)

        binding.completeTv.text = title
        binding.infoTextTv.text = text

        binding.checkBtn.setOnClickListener {
            Timber.e("binding.checkBtn")
            onClickedCheck()
            dismiss()
        }

        binding.checkMyActivityLl.setOnClickListener {
            dismiss()
            onClickedMyActivity()
        }
    }

}