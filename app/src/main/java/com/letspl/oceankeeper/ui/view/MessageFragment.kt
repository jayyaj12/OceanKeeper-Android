package com.letspl.oceankeeper.ui.view

import CustomSpinnerCrewNicknameAdapter
import CustomSpinnerMessageTypeAdapter
import CustomSpinnerProjectNameAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.letspl.oceankeeper.data.model.MessageModel
import com.letspl.oceankeeper.ui.adapter.MessageListAdapter
import com.letspl.oceankeeper.ui.adapter.MessageReceiveCrewAdapter
import com.letspl.oceankeeper.ui.dialog.ProgressDialog
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.MessageEnterType
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.databinding.FragmentMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import timber.log.Timber

@AndroidEntryPoint
class MessageFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var backPressedTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            activity.showInfoMsg("앱을 종료하려면 뒤로가기 버튼을\n한 번 더 눌러주세요")
        } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
            activity.finish();
        }
    }

    lateinit var progressDialog: ProgressDialog
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageListAdapter: MessageListAdapter
    private val messageViewModel: MessageViewModel by viewModels()
    private val activity by lazy {
        requireActivity() as BaseActivity
    }
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var messageReceiveCrewAdapter: MessageReceiveCrewAdapter
    private lateinit var crewNicknameSpinner: CustomSpinnerCrewNicknameAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(layoutInflater)
        binding.messageFragment = this
        binding.messageViewModel = messageViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNoteListAdapter()
        setupViewModelObserver()
        onChangeItemTab()
        setupProgressDialog()
        setupMessageReceiveCrewAdapterRecyclerView()

        messageViewModel.getMessage("ALL") // 메세지 조회
        messageViewModel.getActivityNameList()
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.show()
    }

    private fun setupViewModelObserver() {
        messageViewModel.sendMessageResult.observe(viewLifecycleOwner) {
            if (it) {
                bottomSheetDialog.dismiss()
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }

        messageViewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                bottomSheetView.findViewById<AppCompatButton>(R.id.message_content_et).text = ""
                activity.showErrorMsg(it)
                if (::bottomSheetView.isInitialized) {
                    showErrorMsg(it)
                }
            }
        }

        messageViewModel.getMessageResult.observe(viewLifecycleOwner) {
            it?.let {
                messageListAdapter.submitList(it.toMutableList())
                progressDialog.dismiss()
                binding.messageRv.smoothScrollToPosition(0)
            }
        }

        messageViewModel.getCrewNicknameList.observe(viewLifecycleOwner) {
            setupSendMessageBottomSheetDialog(
                messageViewModel.getActivityNameSaveList(),
                messageViewModel.getCrewList()
            )
        }

        messageViewModel.getCrewNicknameList2.observe(viewLifecycleOwner) {
            crewNicknameSpinner.setMenuList(messageViewModel.getCrewList())
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
            if (!binding.messageRv.canScrollVertically(1)) {
                if (!messageViewModel.isMessageLast()) {
                    progressDialog.show()
                    messageViewModel.getMessage(messageViewModel.getTypeTabItem()) // 메세지 조회
                }
            }
        }
    }

    // 상단 탭 변경
    private fun onChangeItemTab() {
        binding.itemTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // tab 상태가 변경됨
                Timber.e("pos ${tab?.position}")
                when (tab?.position) {
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
    private fun setupMessageReceiveCrewAdapterRecyclerView() {

        messageReceiveCrewAdapter = MessageReceiveCrewAdapter(messageViewModel)
        crewNicknameSpinner = CustomSpinnerCrewNicknameAdapter(
            requireContext(),
            R.layout.spinner_outer_layout,
            messageViewModel.getCrewList(),
            messageViewModel
        )
    }

    // 메세지 전송 bottomSheetDialog 표시
    fun showSendMessageDialog() {
        if (::bottomSheetDialog.isInitialized) {
            bottomSheetDialog.show()
        }
    }

    private fun showErrorMsg(msg: String) {
        bottomSheetView.findViewById<ConstraintLayout>(R.id.error_cl).visibility = View.VISIBLE
        bottomSheetView.findViewById<TextView>(R.id.error_msg_tv).text = msg
        Handler(Looper.myLooper()!!).postDelayed({
            bottomSheetView.findViewById<ConstraintLayout>(R.id.error_cl).visibility = View.GONE
        }, 3000)
    }

    // 메세지 전송 bottomDialogSheet 세팅
    private fun setupSendMessageBottomSheetDialog(
        projectNameList: List<MessageModel.MessageSpinnerProjectNameItem>,
        crewNicknameList: List<MessageModel.MessageSpinnerCrewNicknameItem>
    ) {

        val listMessageType: List<MessageModel.MessageSpinnerMessageTypeItem> = listOf(
            MessageModel.MessageSpinnerMessageTypeItem("활동공지", true),
            MessageModel.MessageSpinnerMessageTypeItem("개인쪽지", false)
        )

        bottomSheetView = layoutInflater.inflate(R.layout.dialog_send_message, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).adapter =
            CustomSpinnerMessageTypeAdapter(
                requireContext(),
                R.layout.spinner_outer_layout,
                listMessageType,
                messageViewModel
            )
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).adapter =
            CustomSpinnerProjectNameAdapter(
                requireContext(),
                R.layout.spinner_outer_layout,
                projectNameList,
                messageViewModel
            )

        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).adapter = crewNicknameSpinner
        crewNicknameSpinner.setMenuList(crewNicknameList)

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false
        bottomSheetDialog.behavior.skipCollapsed = true

        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner)
            .setOnTouchListener { v, event ->
                messageViewModel.setIsClickedMessageType(true)
                false
            }

        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner)
            .setOnTouchListener { v, event ->
                messageViewModel.setIsClickedProjectName(true)
                false
            }

        bottomSheetView.findViewById<RecyclerView>(R.id.receive_rv)?.adapter =
            messageReceiveCrewAdapter

        messageReceiveCrewAdapter.setNicknameArr(crewNicknameList as ArrayList<MessageModel.MessageSpinnerCrewNicknameItem>)
        messageReceiveCrewAdapter.notifyDataSetChanged()

        getEnterType(bottomSheetDialog)

        // 쪽지 유형 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (messageViewModel.isClickedMessageType()) {
                        Timber.e("message_type_spinner")
                        messageViewModel.setTypeSpinnerClickedItemPos(position)
                        when (position) {
                            0 -> {
                                messageViewModel.setTypeSpinnerClickedItemPos(0)
                                bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility =
                                    View.GONE
                                bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).visibility =
                                    View.GONE
                                messageViewModel.clearReceiveList()
                                messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
                            }

                            1 -> {
                                messageViewModel.setTypeSpinnerClickedItemPos(1)
                                messageViewModel.setMessageEnterType(MessageEnterType.UserToUser)
                                bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility =
                                    View.VISIBLE
                                bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).visibility =
                                    View.VISIBLE
                            }
                        }

                        messageViewModel.clearMessageSpinnerClick()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Timber.e("test")
                }
            }

        // 활동 프로젝트 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (messageViewModel.isClickedProjectName()) {
                        Timber.e("my_project_spinner")
                        when (messageViewModel.getMessageEnterType()) {
                            MessageEnterType.ActivityMessage -> {
                                messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
                            }

                            MessageEnterType.UserToUser -> {
                                messageViewModel.setMessageEnterType(MessageEnterType.UserToUser)
                            }

                            else -> false
                        }
                        Timber.e("messageViewModel.setActivityNameSpinnerClickPos(position) $position")
                        messageViewModel.setActivityNameSpinnerClickPos(position)
                        messageViewModel.getCrewNickName2(messageViewModel.getActivityNameSpinnerClickActivityId())

                        messageViewModel.clearMessageSpinnerClick()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Timber.e("test")
                }
            }

        // 전송 버튼 클릭
        bottomSheetView.findViewById<AppCompatButton>(R.id.send_btn).setOnClickListener {
            Timber.e("send_btn")
            Timber.e(
                "bottomSheetView.findViewById<EditText>(R.id.message_content_et).text ${
                    bottomSheetView.findViewById<EditText>(
                        R.id.message_content_et
                    ).text
                }"
            )
            if (bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString() != "") {
                Timber.e("messageViewModel.getTypeSpinnerClickedItem() ${messageViewModel.getTypeSpinnerClickedItem()}")
                when (messageViewModel.getTypeSpinnerClickedItem()) {
                    "NOTICE" -> {
                        if (messageViewModel.getConvertFromMessageCrewItemToStringForNickname()
                                .isNotEmpty()
                        ) {
                            messageViewModel.postMessage(
                                messageViewModel.getActivityNameSpinnerClickActivityId(),
                                bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
                                messageViewModel.getConvertFromMessageCrewItemToStringForNickname(),
                                messageViewModel.getTypeSpinnerClickedItem()
                            )
                        } else {
                            showErrorMsg("해당 프로젝트는 신청한 인원이 없습니다.")
                        }
                    }

                    "PRIVATE" -> {
                        if (messageViewModel.getReceiveList().isNotEmpty()) {
                            messageViewModel.postMessage(
                                messageViewModel.getActivityNameSpinnerClickActivityId(),
                                bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
                                messageViewModel.getReceiveList(),
                                messageViewModel.getTypeSpinnerClickedItem()
                            )
                        } else {
                            showErrorMsg("받는 사람을 1명 이상 선택해 주세요.")
                        }
                    }
                }
            } else {
                showErrorMsg("메세지 내용을 작성해 주세요.")
            }
        }

        // 닫기 버튼 클릭
        bottomSheetView.findViewById<ImageView>(R.id.close_btn).setOnClickListener {
            Timber.e("close_btn ")
            bottomSheetDialog.dismiss()
            messageViewModel.clearSendMessageDialog()
        }
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
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
        Timber.e("messageViewModel.getMessageEnterType() ${messageViewModel.getMessageEnterType()}")
        when (messageViewModel.getMessageEnterType()) {
            // 일반 활동 공지 쪽지
            MessageEnterType.ActivityMessage -> {
                bottomSheetDialog.findViewById<TextView>(R.id.receive_tv)?.visibility = View.GONE
                bottomSheetDialog.findViewById<Spinner>(R.id.receive_spinner)?.visibility =
                    View.GONE
            }
            // 개인쪽지
            MessageEnterType.UserToUser -> {
                bottomSheetDialog.findViewById<TextView>(R.id.receive_tv)?.visibility = View.VISIBLE
                bottomSheetDialog.findViewById<Spinner>(R.id.receive_spinner)?.visibility =
                    View.VISIBLE
            }

            else -> false
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
        messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
        messageViewModel.clearMessageList()
        messageViewModel.clearErrorMsg()
    }
}