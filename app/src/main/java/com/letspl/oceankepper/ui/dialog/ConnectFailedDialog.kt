package com.letspl.oceankepper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.letspl.oceankepper.databinding.DialogAreaChoiceBinding
import com.letspl.oceankepper.databinding.DialogCancelApplyBinding
import com.letspl.oceankepper.databinding.DialogFailedNotificationAlarmBinding
import com.letspl.oceankepper.databinding.DialogLogoutBinding
import com.letspl.oceankepper.databinding.DialogRecruitActivitiyCompleteBinding
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ConnectFailedDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogFailedNotificationAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFailedNotificationAlarmBinding.inflate(layoutInflater)
        binding.connectFailedDialog = this
        setContentView(binding.root)
    }

    fun onClickCheck() {
        dismiss()
    }

}