package com.letspl.oceankepper.ui.view

import CustomSpinnerMessageTypeAdapter
import CustomSpinnerProjectNameAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.databinding.FragmentMessageBinding
import com.letspl.oceankepper.ui.adapter.MessageListAdapter
import com.letspl.oceankepper.ui.dialog.ProgressDialog
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment: Fragment() {

    @Inject
    lateinit var progressDialog: ProgressDialog
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageListAdapter: MessageListAdapter
    private val messageViewModel: MessageViewModel by viewModels()
    private val activity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNoteListAdapter()
        setupViewModelObserver()
        onChangeItemTab()
        setupDialogSendMessageSpinner()

        progressDialog.show()
        messageViewModel.getMessage("ALL") // 메세지 조회
        messageViewModel.getActivityNameList()
    }

    private fun setupViewModelObserver() {
        messageViewModel.getMessageResult.observe(viewLifecycleOwner) {
            Timber.e("getMessageResult")
            it?.let {
                messageListAdapter.submitList(it.toMutableList())
                progressDialog.dismiss()
                binding.messageRv.smoothScrollToPosition(0)
            }
        }
        messageViewModel.getProjectNameList.observe(viewLifecycleOwner) {
            it?.let { projectList ->
                val list = arrayListOf<MessageModel.MessageSpinnerProjectNameItem>()
                projectList.forEach {data ->
                    list.add(MessageModel.MessageSpinnerProjectNameItem(
                        data.activityId, data.title, false
                    ))
                }
                setupSendMessageBottomSheetDialog(list)
            }
        }
    }

    // 메세지 ListAdapter
    private fun setupNoteListAdapter() {
        messageListAdapter = MessageListAdapter(messageViewModel) {
            messageViewModel.saveClickedMessageItem(it)
            activity.onReplaceFragment(MessageDetailFragment(), false, false)
        }

        binding.messageRv.adapter = messageListAdapter
        binding.messageRv.addItemDecoration(
            DividerItemDecoration(requireContext(), VERTICAL)
        )

        binding.messageRv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.messageRv.canScrollVertically(1)) {
                Timber.e("canScrollVertically")
                if(!messageViewModel.isMessageLast()) {
                    progressDialog.show()
                    messageViewModel.getMessage(messageViewModel.getTypeTabItem()) // 메세지 조회
                }
            }
        }
    }

    // 상단 탭 변경
    private fun onChangeItemTab() {
        binding.itemTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // tab 상태가 변경됨
                Timber.e("pos ${tab?.position}")
                when(tab?.position) {
                    0 -> messageViewModel.changeTypeTabItem("ALL")
                    1 -> messageViewModel.changeTypeTabItem("NOTICE")
                    2 -> messageViewModel.changeTypeTabItem("PRIVATE")
                    3 -> messageViewModel.changeTypeTabItem("SENT")
                }

                messageViewModel.clearMessageList()
                progressDialog.show()
                messageViewModel.getMessage(messageViewModel.getTypeTabItem())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Tab 의 상태가 선택되지 않음으로 변경
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 클릭됨
            }
        })
    }

    private fun setupSendMessageBottomSheetDialog(projectNameList: List<MessageModel.MessageSpinnerProjectNameItem>) {
        val bottomSheetView = layoutInflater.inflate(R.layout.dialog_send_message, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val listMessageType: List<MessageModel.MessageSpinnerMessageTypeItem> = listOf(MessageModel.MessageSpinnerMessageTypeItem("활동공지", false),MessageModel.MessageSpinnerMessageTypeItem("개인쪽지", true),MessageModel.MessageSpinnerMessageTypeItem("거절쪽지", false))
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).adapter = CustomSpinnerMessageTypeAdapter(requireContext(), R.layout.spinner_outer_layout, listMessageType, messageViewModel)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDialog.show()

        val listProjectName: List<MessageModel.MessageSpinnerProjectNameItem> = projectNameList
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).adapter = CustomSpinnerProjectNameAdapter(requireContext(), R.layout.spinner_outer_layout, listProjectName, messageViewModel)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDialog.show()

        // 쪽지 유형 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                messageViewModel.setTypeSpinnerClickedItemPos(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("test")
            }
        }

        // 활동 프로젝트 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                messageViewModel.setActivityNameSpinnerClickPos(position)
                messageViewModel.getCrewNickName(messageViewModel.getActivityNameSpinnerClickActivityId())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("test")
            }
        }

        // 전송 버튼 클릭
        bottomSheetView.findViewById<AppCompatButton>(R.id.send_btn).setOnClickListener {
            messageViewModel.postMessage(
                bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString()
            )
        }
    }

    private fun setupDialogSendMessageSpinner() {
//        binding.sendMessageSpinner.adapter =
    }

    override fun onDestroyView() {
        super.onDestroyView()

        messageViewModel.clearMessageList()
    }
}