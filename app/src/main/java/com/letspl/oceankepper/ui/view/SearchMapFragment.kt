package com.letspl.oceankepper.ui.view

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.letspl.oceankepper.databinding.FragmentSearchMapBinding
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class SearchMapFragment: Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentSearchMapBinding? = null
    private val binding: FragmentSearchMapBinding get() = _binding!!
    companion object {
        var activityRecruitViewModel: ActivityRecruitViewModel? = null
    }

    val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRecruitViewModel = ViewModelProvider(this)[ActivityRecruitViewModel::class.java]
        _binding = FragmentSearchMapBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.searchMapFragment = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJavaScriptInterface()

        // 지도 검색 결과값
        activityRecruitViewModel?.searchMap?.observe(viewLifecycleOwner) {
            // 위경도 값 가져온 후 저장 
            activityRecruitViewModel!!.findGeoPoint(it)
            activity.onReplaceFragment(ActivityRecruitFragment())
        }
    }

    private fun setupJavaScriptInterface() {
        binding.wvSearchAddress.settings.javaScriptEnabled = true
        binding.wvSearchAddress.settings.domStorageEnabled = true
        binding.wvSearchAddress.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.wvSearchAddress.settings.setSupportMultipleWindows(true)
        binding.wvSearchAddress.addJavascriptInterface(AndroidBridge(), "Android")

        binding.wvSearchAddress.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                Timber.e("onReceivedSslError")
                handler?.proceed()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Timber.e("shouldOverrideUrlLoading")
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Timber.e("onPageStarted")
                super.onPageStarted(view, url, favicon)
                binding.webProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Timber.e("onPageFinished")
                super.onPageFinished(view, url)
                binding.webProgress.visibility = View.GONE
                binding.wvSearchAddress.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }
        binding.wvSearchAddress.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                Timber.e("onPermissionRequest")
                request?.grant(request?.resources)
            }
        }

        binding.wvSearchAddress.loadUrl("https://oceankeeper-test.s3.ap-northeast-2.amazonaws.com/address/daum_address.html")
    }

    fun onClickedCloseBtn() {
        activity.onReplaceFragment(ActivityRecruitFragment())
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(ActivityRecruitFragment())
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    class AndroidBridge {
        @JavascriptInterface
        @SuppressWarnings("unused")
        fun processDATA(data: String?) {
            Timber.e("data $data")
            activityRecruitViewModel?.setSearchMapResult(data ?: "")
        }
    }
}