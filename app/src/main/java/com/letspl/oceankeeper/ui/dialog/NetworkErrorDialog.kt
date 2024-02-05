package com.letspl.oceankeeper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.letspl.oceankeeper.databinding.DialogLogoutBinding
import com.letspl.oceankeeper.databinding.DialogNetworkErrorBinding
import com.letspl.oceankeeper.databinding.DialogServerErrorBinding

class NetworkErrorDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogNetworkErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNetworkErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)

        binding.checkBtn.setOnClickListener {
            dismiss()
        }
    }
}