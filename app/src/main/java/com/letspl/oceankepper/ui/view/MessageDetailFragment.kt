package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.databinding.FragmentMessageDetailBinding
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageDetailFragment : Fragment() {

    private var _binding: FragmentMessageDetailBinding? = null
    val binding:FragmentMessageDetailBinding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.messageDetailFragment = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadMessageItem()

    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(MessageFragment(), false, true)
    }

    // 메세지 아이템 불러오기
    private fun setupLoadMessageItem() {
        val item = messageViewModel.getClickedMessageItem()
        binding.run {
            garbageCategoryTv.text = messageViewModel.getGarbageCategory(item.garbageCategory)
            titleTv.text = item.activityTitle
            activityDateTv.text = messageViewModel.convertIso8601YYYYToCustomFormat(item.activityStartAt)
            messageAtTv.text = "${messageViewModel.convertAMPMToCustomFormat(item.messageSentAt)} 보냄"
            fromTv.text = messageViewModel.getFromWho(item.from)
            contentTv.text = item.messageBody
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}