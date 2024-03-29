package com.letspl.oceankeeper.ui.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.databinding.ActivityBaseBinding
import com.letspl.oceankeeper.ui.dialog.NetworkErrorDialog
import com.letspl.oceankeeper.ui.dialog.ServerErrorDialog
import com.letspl.oceankeeper.ui.dialog.TokenNotExistDialog
import com.letspl.oceankeeper.ui.viewmodel.BaseViewModel
import com.letspl.oceankeeper.util.ContextUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.MessageDigest


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private val baseViewModel: BaseViewModel by viewModels()

    interface OnBackPressedListener {
        fun onBackPressed()
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                (fragment as OnBackPressedListener).onBackPressed()
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ContextUtil.context = this
        setupSplashFragment()
        getRegisterFcmToken()

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home_icon -> {
                    onReplaceFragment(MainFragment(), false, true)
                    return@setOnItemSelectedListener true
                }
                R.id.msg_icon -> {
                    onReplaceFragment(MessageFragment(), false, true)
                    return@setOnItemSelectedListener true
                }
                R.id.my_activity_icon -> {
                    onReplaceFragment(MyActivityFragment(), false, true)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener true
            }
        }
    }

    // firebase fcm token 가져오기 및 저장
    private fun getRegisterFcmToken() {
        // 등록된 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            Timber.e("register Token ${task.result}")

            baseViewModel.setFcmDeviceToken(task.result)
        })
    }

    // splash setUp
    private fun setupSplashFragment() {
        onReplaceFragment(SplashFragment(), false)
    }

    // fragment 변경
    fun onReplaceFragment(fragment: Fragment?, addToBackStack: Boolean = true, isVisibleBottomNav: Boolean = false, navItem: Int = -1) {
        if(!::binding.isInitialized) {
            binding = ActivityBaseBinding.inflate(layoutInflater)
        }

        when(navItem) {
            1 -> binding.bottomNav.selectedItemId = R.id.home_icon
            2 -> binding.bottomNav.selectedItemId = R.id.msg_icon
            3 -> binding.bottomNav.selectedItemId = R.id.my_activity_icon
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                if(fragment != null) {
                    supportFragmentManager.beginTransaction().let {
                        it.replace(R.id.fragment_container, fragment)
                        if (addToBackStack) {
                            it.addToBackStack(null)
                        }
                        it.commitAllowingStateLoss()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                if(isVisibleBottomNav) {
                    binding.bottomNav.visibility = View.VISIBLE
                } else {
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }

    // 에러 메세지 표시
    fun showErrorMsg(msg: String) {
        if(msg == "not Connected Network") {
            NetworkErrorDialog(this).show()
        } else if(msg == "Failed to connect to /13.125.91.17:8080") {
            // 서버 죽었을 때
            ServerErrorDialog(this).show()
        } else if(msg == "토큰 없음") {
            // 서버 죽었을 때
            TokenNotExistDialog(this){
              onReplaceFragment(LoginFragment(), false, false)
            }.show()
        } else {
            binding.errorCl.visibility = View.VISIBLE
            binding.errorMsgTv.text = msg
            Handler(Looper.myLooper()!!).postDelayed({
                binding.errorCl.visibility = View.GONE
            }, 3000)
        }
    }

    // 성공 메세지 표시
    fun showSuccessMsg(msg: String) {
        binding.sucessCl.visibility = View.VISIBLE
        binding.successMsgTv.text = msg
        Handler(Looper.myLooper()!!).postDelayed({
            binding.sucessCl.visibility = View.GONE
        }, 3000)
    }
}