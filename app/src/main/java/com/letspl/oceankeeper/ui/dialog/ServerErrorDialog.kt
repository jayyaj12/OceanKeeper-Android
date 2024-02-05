package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogLogoutBinding
import com.letspl.oceankeeper.databinding.DialogServerErrorBinding

class ServerErrorDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogServerErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogServerErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)

        binding.checkBtn.setOnClickListener {
            dismiss()
        }
    }
}