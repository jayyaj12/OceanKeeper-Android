package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.databinding.FragmentMessageDetailBinding
import com.letspl.oceankepper.ui.dialog.DeleteMessageDialog
import com.letspl.oceankepper.ui.dialog.ReplyMessageDialog
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageDetailFragment : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentMessageDetailBinding? = null
    val binding:FragmentMessageDetailBinding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        onClickedBackBtn()
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
        setupViewModelObserver()
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(MessageFragment(), false, true)
    }

    private fun setupViewModelObserver() {
        messageViewModel.sendMessageResult.observe(viewLifecycleOwner) {
            if(it) {
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }
        messageViewModel.deleteMessageResult.observe(viewLifecycleOwner) {
            if(it) {
                onClickedBackBtn()
            }
        }
    }

    // 답장하기 버튼 클릭
    fun onClickReplyBtn() {
        ReplyMessageDialog(requireContext()) {content ->
            val item = messageViewModel.getClickedMessageItem()

            messageViewModel.postMessage(item.activityId, content, arrayListOf(item.from), "PRIVATE")
        }.show()
    }

    // 삭제버튼 클릭
    fun onClickDeleteBtn() {
        DeleteMessageDialog(requireContext()) {
            val item = messageViewModel.getClickedMessageItem()

            messageViewModel.deleteMessage(item.id.toInt())
        }.show()
    }

    // 메세지 아이템 불러오기
    private fun setupLoadMessageItem() {
        val item = messageViewModel.getClickedMessageItem()

        // 읽음 상태로 변경
        messageViewModel.postMessageDetail(item.id)

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

        messageViewModel.clearMessageLiveData()
        _binding = null
    }
}