package com.letspl.oceankeeper.ui.view

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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.model.MessageModel
import com.letspl.oceankeeper.databinding.FragmentMessageBinding
import com.letspl.oceankeeper.ui.adapter.MessageListAdapter
import com.letspl.oceankeeper.ui.adapter.MessageReceiveCrewAdapter
import com.letspl.oceankeeper.ui.dialog.ProgressDialog
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.MessageEnterType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var messageReceiveCrewAdapter : MessageReceiveCrewAdapter

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
            if(it) {
                bottomSheetDialog.dismiss()
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }

        messageViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
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
            setupSendMessageBottomSheetDialog(messageViewModel.getActivityNameSaveList(), it)
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
    private fun setupMessageReceiveCrewAdapterRecyclerView() {

        messageReceiveCrewAdapter = MessageReceiveCrewAdapter(messageViewModel)
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


        Timber.e("projectNameList $projectNameList")
        Timber.e("crewNicknameList $crewNicknameList")

        val listMessageType: List<MessageModel.MessageSpinnerMessageTypeItem> = listOf(MessageModel.MessageSpinnerMessageTypeItem("활동공지", false),MessageModel.MessageSpinnerMessageTypeItem("개인쪽지", true))
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).adapter = CustomSpinnerMessageTypeAdapter(requireContext(), R.layout.spinner_outer_layout, listMessageType, messageViewModel)
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).adapter = CustomSpinnerProjectNameAdapter(requireContext(), R.layout.spinner_outer_layout, projectNameList, messageViewModel)

//        val crewNicknameSpinner = CustomSpinnerCrewNicknameAdapter(requireContext(), R.layout.spinner_outer_layout, messageViewModel.getCrewList(), messageViewModel)
//        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).adapter = crewNicknameSpinner
//        crewNicknameSpinner.notifyDataSetChanged()

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false
        bottomSheetDialog.behavior.skipCollapsed = true

        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).setOnTouchListener { v, event ->
            messageViewModel.setIsClickedMessageType(true)
            false
        }

        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).setOnTouchListener { v, event ->
            messageViewModel.setIsClickedProjectName(true)
            false
        }

        bottomSheetView.findViewById<RecyclerView>(R.id.receive_rv)?.adapter = messageReceiveCrewAdapter

        messageReceiveCrewAdapter.setNicknameArr(crewNicknameList as ArrayList<MessageModel.MessageSpinnerCrewNicknameItem>)
        messageReceiveCrewAdapter.notifyDataSetChanged()

//        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).setOnTouchListener { v, event ->
//            messageViewModel.setIsClickedReceive(true)
//            false
//        }

        getEnterType(bottomSheetDialog)

        // 쪽지 유형 선택 시
        bottomSheetView.findViewById<Spinner>(R.id.message_type_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(messageViewModel.isClickedMessageType()) {
                    messageViewModel.setTypeSpinnerClickedItemPos(position)
                    when (position) {
                        0 -> {
                            bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility =
                                View.GONE

                            messageViewModel.clearReceiveList()
                            messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
                        }

                        1 -> {
                            messageViewModel.setMessageEnterType(MessageEnterType.UserToUser)
                            bottomSheetView.findViewById<TextView>(R.id.receive_tv).visibility =
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
        bottomSheetView.findViewById<Spinner>(R.id.my_project_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(messageViewModel.isClickedProjectName()) {
                     when (messageViewModel.getMessageEnterType()) {
                        MessageEnterType.ActivityMessage -> {
                            messageViewModel.setMessageEnterType(MessageEnterType.ActivityMessage)
                        }

                        MessageEnterType.UserToUser -> {
                            messageViewModel.setMessageEnterType(MessageEnterType.UserToUser)
                        }

                        else -> false
                    }
                    messageViewModel.setActivityNameSpinnerClickPos(position)
                    messageViewModel.getCrewNickName(messageViewModel.getActivityNameSpinnerClickActivityId())

                    messageViewModel.clearMessageSpinnerClick()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.e("test")
            }
        }

        // 받는 사람 선택 시
//        bottomSheetView.findViewById<Spinner>(R.id.receive_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                Timber.e("messageViewModel.isClickedReceive() ${messageViewModel.isClickedReceive()}")
//
//                if(messageViewModel.isClickedReceive()) {
//                    Timber.e("crewNicknameList[position].nickname ${crewNicknameList[position].nickname}")
//                    messageViewModel.setReceiveList(crewNicknameList[position].nickname)
//                    messageViewModel.setActivityNameSpinnerClickPos(position)
////                    messageViewModel.setCrewNicknameListChecked(position)
//                    Timber.e("")
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Timber.e("test")
//            }
//        }

        // 전송 버튼 클릭
        bottomSheetView.findViewById<AppCompatButton>(R.id.send_btn).setOnClickListener {
            if(bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString() != ""){
                when(messageViewModel.getTypeSpinnerClickedItem()) {
                    "NOTICE" -> {
//                        messageViewModel.postMessage(
//                            messageViewModel.getActivityNameSpinnerClickActivityId(),
//                            bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
//                            messageViewModel.getConvertFromMessageCrewItemToStringForNickname(),
//                            messageViewModel.getTypeSpinnerClickedItem()
//                        )
                    }
                    "PRIVATE" -> {
                        if(messageViewModel.getConvertFromMessageCrewItemToStringForNickname().size == 1) {
                            Timber.e("getConvertFromMessageCrewItemToStringForNickname ${messageViewModel.getConvertFromMessageCrewItemToStringForNickname()}")
                        } else {
                            Timber.e("getReceiveList ${messageViewModel.getReceiveList()}")

                        }
//                        messageViewModel.postMessage(
//                            messageViewModel.getActivityNameSpinnerClickActivityId(),
//                            bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
//                            messageViewModel.getConvertFromMessageCrewItemToStringForNickname(),
//                            messageViewModel.getTypeSpinnerClickedItem()
//                        )
                    }
                }
//                messageViewModel.postMessage(
//                    messageViewModel.getActivityNameSpinnerClickActivityId(),
//                    bottomSheetView.findViewById<EditText>(R.id.message_content_et).text.toString(),
//                    messageViewModel.getConvertFromMessageCrewItemToStringForNickname(),
//                    messageViewModel.getTypeSpinnerClickedItem()
//                )
            } else {
                activity.showErrorMsg("메세지 내용을 작성해주세요.")
            }

        }

        // 닫기 버튼 클릭
        bottomSheetView.findViewById<ImageView>(R.id.close_btn).setOnClickListener {
            Timber.e("close_btn ")
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
        Timber.e("messageViewModel.getMessageEnterType() ${messageViewModel.getMessageEnterType()}")
        when(messageViewModel.getMessageEnterType()) {
            // 일반 활동 공지 쪽지
            MessageEnterType.ActivityMessage -> {
                bottomSheetDialog.findViewById<TextView>(R.id.receive_tv)?.visibility = View.GONE
                bottomSheetDialog.findViewById<Spinner>(R.id.receive_spinner)?.visibility = View.GONE
            }
            // 개인쪽지
            MessageEnterType.UserToUser -> {
                bottomSheetDialog.findViewById<TextView>(R.id.receive_tv)?.visibility = View.VISIBLE
                bottomSheetDialog.findViewById<Spinner>(R.id.receive_spinner)?.visibility = View.VISIBLE
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