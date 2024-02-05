package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogLogoutBinding
import com.letspl.oceankeeper.databinding.DialogServerErrorBinding
import com.letspl.oceankeeper.databinding.DialogTokenNotExistErrorBinding

class TokenNotExistDialog(context: Context, private val onClickChecked: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogTokenNotExistErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTokenNotExistErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)

        binding.checkBtn.setOnClickListener {
            dismiss()
            onClickChecked()
        }
    }
}