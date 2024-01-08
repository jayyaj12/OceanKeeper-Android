package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.data.dto.GetMyActivityDetailLocation
import com.letspl.oceankepper.databinding.FragmentActivityDetailBinding
import com.letspl.oceankepper.ui.dialog.InquireMessageDialog
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import com.letspl.oceankepper.util.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

@AndroidEntryPoint
class ActivityDetailFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private var _binding: FragmentActivityDetailBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    override fun onBackPressed() {
        when(EntryPoint.activityDetail) {
            "main" -> activity.onReplaceFragment(MainFragment(), false, true)
            "myActivity" -> activity.onReplaceFragment(MyActivityFragment(), false, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.activityDetailFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        setupViewModelObserver()
    }

    // 상세 표시할 활동의 정보를 불러옴
    private fun loadData() {
        mainViewModel.getMyActivityDetail(mainViewModel.getClickedActivityId())
    }

    private fun setupViewModelObserver() {
        mainViewModel.activityDetailSelectResult.observe(viewLifecycleOwner) {
            it?.let {
                setupKakaoMap(it.response.location)
            }
        }
        mainViewModel.errorMsg.observe(viewLifecycleOwner) {
            activity.showErrorMsg(it)
        }

        messageViewModel.sendMessageResult.observe(viewLifecycleOwner) {
            if(it) {
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }
    }

    // 뒤로가기 버튼 클릭 시
    fun onClickedPreviousBtn() {
        when(EntryPoint.activityDetail) {
            "main" -> activity.onReplaceFragment(MainFragment(), false, true)
            "myActivity" -> activity.onReplaceFragment(MyActivityFragment(), false, true)
        }
    }

    // 지원하기 버튼 클릭 시
    fun onClickedApplyBtn() {
        activity.onReplaceFragment(ActivityApplyFragment())
    }

    // 문의하기 버튼 클릭 시
    fun onClickedInquireBtn() {
        val getClickItem = mainViewModel.getClickedActivityItem()
        InquireMessageDialog(requireContext(), getClickItem.hostNickName) {contnet ->
            messageViewModel.postMessage(mainViewModel.getClickedActivityId(), contnet, arrayListOf(getClickItem.hostNickName), "PRIVATE")
        }.show()
    }

    private fun setupKakaoMap(location: GetMyActivityDetailLocation) {
        val mapView = MapView(requireActivity())
        binding.locationMap.addView(mapView)
        //수원 화성의 위도, 경도
        val mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)

        //지도의 중심점을 수원 화성으로 설정, 확대 레벨 설정 (값이 작을수록 더 확대됨)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setZoomLevel(1, true)

        //마커 생성
        val marker = MapPOIItem()
        marker.itemName = "이곳이 수원 화성입니다"
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        mapView.addPOIItem(marker)

        // 스크롤뷰 안에 mapView 가 들어갈 경우 지도 이동과 관련된 작업이 원활하게 진행되지 않음. 따라 우선순위를 빼앗아오는 코드 작성이 필요
        mapView.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                    binding.detailSv.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener false
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        messageViewModel.clearMessageList()

        _binding = null
    }
}