package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentCrewDetailBinding
import com.letspl.oceankeeper.ui.viewmodel.ManageApplyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrewDetailFragment(private val applicationId: String, private val activityId: String) : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentCrewDetailBinding? = null
    private val binding: FragmentCrewDetailBinding get() = _binding!!
    private val manageApplyViewModel: ManageApplyViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        onClosePage()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrewDetailBinding.inflate(layoutInflater)
        binding.manageApplyViewModel = manageApplyViewModel
        binding.crewDetailFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageApplyViewModel.getCrewDetail(applicationId)
    }

    fun onClosePage() {
        activity.onReplaceFragment(ManageApplyMemberFragment(activityId))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}