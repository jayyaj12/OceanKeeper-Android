package com.letspl.oceankepper.ui.view

import CustomSpinnerCrewNicknameAdapter
import CustomSpinnerMessageTypeAdapter
import CustomSpinnerProjectNameAdapter
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.databinding.FragmentMessageBinding
import com.letspl.oceankepper.ui.adapter.MessageListAdapter
import com.letspl.oceankepper.ui.adapter.MessageReceiveCrewAdapter
import com.letspl.oceankepper.ui.dialog.ProgressDialog
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import com.letspl.oceankepper.util.EntryPoint
import com.letspl.oceankepper.util.MessageEnterType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment: Fragment(), BaseActivity.OnBackPressedListener {

    override fun onBackPressed() {
        activity.finish()
    }

    lateinit var progressDialog: ProgressDialog
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageListAdapter: MessageListAdapter
    private val messageViewModel: MessageViewModel by viewModels()
    private val activity by lazy {
        requireActivity() as BaseActivity
    }
    private lateinit var messageReceiveCrewAdapter: MessageReceiveCrewAdapter
    private lateinit var bottomSheetDialog : BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(layoutInflater)
        binding.messageFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNoteListAdapter()
        setupViewModelObserver()
        onChangeItemTab()
        setupProgressDialog()

        messageViewModel.getMessage("ALL") // 메세지 조회
        messageViewModel.getActivityNameList()
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.show()
    }

    private fun setupViewModelObserver() {
        messageViewModel.sendMessageResult.observe(viewLifecycleOwner) {
            if(it) {
                bottomSheetDialog.dismiss()
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }

        messageViewModel.errorMsg.observe(viewLifecycleOwner) {
            activity.showErrorMsg(it)
        }

        messageViewModel.getMessageResult.observe(viewLifecycleOwner) {
            Timber.e("it ${MessageModel.messageList}")
            it?.let {
                messageListAdapter.submitList(it.toMutableList())
                progressDialog.dismiss()
                binding.messageRv.smoothScrollToPosition(0)
            }
        }

        messageViewModel.getCrewNicknameList.observe(viewLifecycleOwner) {
            setupSendMessageBottomSheetDialog(messageViewModel.getActivityNameSaveList(), it)
            messageReceiveCrewAdapter.setNicknameArr(it as ArrayList<MessageModel.MessageSpinnerCrewNicknameItem>)
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

    // 받는 사람 recyclerview 셋업
    private fun setupMessageReceiveCrewAdapterRecyclerView(bottomSheetDialog: BottomSheetDialog) {
        messageReceiveCrewAdapter = MessageReceiveCrewAdapter(messageViewModel)
        bottomSheetDialog.findViewById<RecyclerView>(R.id.receive_rv)?.adapter = messageReceiveCrewAdapter
    }

    // 메세지 전송 bottomSheetDialog 표시
    fun showSendMessageDialog() {
        if(::bottomSheetDialog.isInitialized) {
            bottomSheetDialog.show()
        }
    }

    // 메세지 전송 bottomDialogSheet 세팅
    private fun setupSendMessageBottomSheetDialog(projectNameList: List<MessageModel.MessageSpinnerProjectNameItem>, crewNicknameList: List<MessageModel.MessageSpinnerCrewNicknameItem>) {
        val bottomSheetView = layoutInflater.inflate(R.layout.dialog_send_message, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        setupMessageReceiveCrewAdapterRecyclerView(bottomSheetDialog)

        val listMessageType: List<MessageModel.MessageSpinnerMessageTypeItem> = listOf(MessageModel.MessageSpinnerMessageTypeItem("활동공지", false),MessageModel.MessageSpinnerMessageTypeItem("개인쪽지", true),MessageModel.MessageSpinnerMessageTypeItem("거절쪽지", false))
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).adapter = CustomSpinnerMessageTypeAdapter(requireContext(), R.layout.spinner_outer_layout, listMessageType, messageViewModel)
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).adapter = CustomSpinnerProjectNameAdapter(requireContext(), R.layout.spinner_outer_layout, projectNameList, messageViewModel)

        val crewNicknameSpinner = CustomSpinnerCrewNicknameAdapter(requireContext(), R.layout.spinner_outer_layout, crewNicknameList, messageViewModel)
        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).adapter = crewNicknameSpinner
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        getEnterType(bottomSheetDialog)

        // 쪽지 유형 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                messageViewModel.setTypeSpinnerClickedItemPos(position)
                when(position) {
                    0 -> {
                        bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility = View.GONE
                        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).visibility = View.GONE

                        messageViewModel.clearReceiveList()
                        messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
//                        messageViewModel.setReceiveList(messageViewModel.getCrewList())
                    }
                    1 -> {
                            messageViewModel.setMessageEnterType(MessageEnterType.UserToUser)
                        bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility = View.VISIBLE
                        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).visibility = View.VISIBLE
                         }
                    2 -> {
                        messageViewModel.setMessageEnterType(MessageEnterType.FromUserToHost)
                        bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility = View.VISIBLE
                        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).visibility = View.VISIBLE
                    }
                }
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

        // 받는 사람 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                messageViewModel.setActivityNameSpinnerClickPos(position)
                messageViewModel.setCrewNicknameListChecked(position)
                crewNicknameSpinner.setMenuList(messageViewModel.getCrewList())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("test")
            }
        }

        // 전송 버튼 클릭
        bottomSheetView.findViewById<AppCompatButton>(R.id.send_btn).setOnClickListener {
            messageViewModel.postMessage(
                messageViewModel.getActivityNameSpinnerClickActivityId(),
                bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
                 messageViewModel.getConvertFromMessageCrewItemToStringForNickname(),
                messageViewModel.getTypeSpinnerClickedItem()
            )
        }

        // 전송 버튼 클릭
        bottomSheetView.findViewById<ImageView>(R.id.close_btn).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 80 / 100
        // 기기 높이 대비 비율 설정 부분!!
        // 위 수치는 기기 높이 대비 80%로 다이얼로그 높이를 설정
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    // enterType 에 따라 Spinner Block 처리함
    private fun getEnterType(bottomSheetDialog: BottomSheetDialog) {
        when(messageViewModel.getMessageEnterType()) {
            // 일반 활동 공지 쪽지
            MessageEnterType.ActivityMessage -> {
            }
            // 활동 프로젝트만 선택 가능한 쪽지
            MessageEnterType.ActivityMessageBlockMessageType -> {
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.setBackgroundResource(R.drawable.custom_radius8_solid_cfd8dc_stroke_b0bec5)
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.isEnabled = false
            }
            // 프로젝트에서 바로 공지할 경우 쪽지유형, 활동 프로젝트 선택 풀가
            MessageEnterType.ActivityMessageDirect -> {
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.setBackgroundResource(R.drawable.custom_radius8_solid_cfd8dc_stroke_b0bec5)
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.isEnabled = false
                bottomSheetDialog.findViewById<Spinner>(R.id.my_project_spinner)?.setBackgroundResource(R.drawable.custom_radius8_solid_cfd8dc_stroke_b0bec5)
                bottomSheetDialog.findViewById<Spinner>(R.id.my_project_spinner)?.isEnabled = false
            }
            // 모집한 사람에게 쪽지 보냄
            MessageEnterType.FromUserToHost -> {
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.setBackgroundResource(R.drawable.custom_radius8_solid_cfd8dc_stroke_b0bec5)
                bottomSheetDialog.findViewById<Spinner>(R.id.message_type_spinner)?.isEnabled = false
                bottomSheetDialog.findViewById<Spinner>(R.id.my_project_spinner)?.setBackgroundResource(R.drawable.custom_radius8_solid_cfd8dc_stroke_b0bec5)
                bottomSheetDialog.findViewById<Spinner>(R.id.my_project_spinner)?.isEnabled = false
            }
            // 개인쪽지
            MessageEnterType.UserToUser -> {
                setupRatio(bottomSheetDialog)
                bottomSheetDialog.show()
            }

            else -> {}
        }

    }

    // 설정 페이지 이동
    fun onMoveSetting() {
        EntryPoint.settingPoint = "message"
        activity.onReplaceFragment(SettingFragment(), false, false)
    }

    // 알림 페이지 이동
    fun onMoveNotification() {
        EntryPoint.notificationPoint = "message"
        activity.onReplaceFragment(NotificationFragment(), false, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        messageViewModel.clearMessageList()
    }
}