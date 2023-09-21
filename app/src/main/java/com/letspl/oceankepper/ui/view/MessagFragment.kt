package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.letspl.oceankepper.data.dto.MessageItemDto
import com.letspl.oceankepper.databinding.FragmentMessageBinding
import com.letspl.oceankepper.ui.adapter.NoteListAdapter
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagFragment: Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteListAdapter: NoteListAdapter
    private val messageViewModel: MessageViewModel by viewModels()

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

        messageViewModel.getMessage() // 메세지 조회
    }

    private fun setupViewModelObserver() {
        messageViewModel.getMessageResult.observe(viewLifecycleOwner) {
            it?.let {
                noteListAdapter.submitList(it.toMutableList())
            }
        }
    }

    private fun setupNoteListAdapter() {
        noteListAdapter = NoteListAdapter(messageViewModel)

        binding.noteRv.adapter = noteListAdapter
        binding.noteRv.addItemDecoration(
            DividerItemDecoration(requireContext(), VERTICAL)
        )

        binding.noteRv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.noteRv.canScrollVertically(1)) {
                messageViewModel.getMessage() // 메세지 조회
            }
        }
    }
}