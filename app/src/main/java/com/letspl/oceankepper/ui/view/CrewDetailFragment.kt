package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentCrewDetailBinding
import com.letspl.oceankepper.ui.viewmodel.ManageApplyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrewDetailFragment(private val applicationId: String) : Fragment() {

    private var _binding: FragmentCrewDetailBinding? = null
    private val binding: FragmentCrewDetailBinding get() = _binding!!
    private val manageApplyViewModel: ManageApplyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrewDetailBinding.inflate(layoutInflater)
        binding.manageApplyViewModel = manageApplyViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageApplyViewModel.getCrewDetail(applicationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}