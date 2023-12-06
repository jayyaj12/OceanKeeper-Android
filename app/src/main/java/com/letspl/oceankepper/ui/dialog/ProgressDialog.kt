package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.letspl.oceankepper.databinding.DialogProgressBinding

class ProgressDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogProgressBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(binding.root)
        binding.spinkit.setIndeterminateDrawable(ThreeBounce())
    }

}