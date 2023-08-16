package com.letspl.oceankepper.ui.view

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.letspl.oceankepper.databinding.FragmentActivityDetailBinding
import net.daum.mf.map.api.MapLayout
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import timber.log.Timber
import java.security.MessageDigest


class ActivityDetailFragment : Fragment() {
    private var _binding: FragmentActivityDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupKakaoMap()
    }

    private fun setupKakaoMap() {
        val mapView = MapView(requireActivity())
        binding.locationMap.addView(mapView)
        //수원 화성의 위도, 경도
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.28730797086605, 127.01192716921177)

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
//        val position = MapPoint.mapPointWithGeoCoord(37.30221019707655, 127.01548644919744 )

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
}