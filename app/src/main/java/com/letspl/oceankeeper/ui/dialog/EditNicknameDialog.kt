package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogChangeNicknameBinding

class EditNicknameDialog(context: Context, private val onClickEdit: (String) -> Unit): Dialog(context) {
    private lateinit var binding: DialogChangeNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogChangeNicknameBinding.inflate(layoutInflater)
        binding.editNicknameDialog = this
        setContentView(binding.root)
        setCancelable(false)

        binding.editBtn.setOnClickListener {
            onClickEdit(binding.nicknameEt.text.toString())
            dismiss()
        }
    }

    fun onClickClose() {
        dismiss()
    }

}