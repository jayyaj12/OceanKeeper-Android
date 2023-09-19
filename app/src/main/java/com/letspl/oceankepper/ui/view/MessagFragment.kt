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
        val list = listOf<MessageItemDto>(
            MessageItemDto("asdas", "중현1", "COASTAL", "tesa1", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트1", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현2", "COASTAL", "tesa2", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트2", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현3", "COASTAL", "tesa3", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트3", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현4", "COASTAL", "tesa4", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트4", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현5", "COASTAL", "tesa5", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트5", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현6", "COASTAL", "tesa6", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트6", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현7", "COASTAL", "tesa7", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트7", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현8", "COASTAL", "tesa8", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트8", "쪽지 내용 테스트"),
            MessageItemDto("asdas", "중현9", "COASTAL", "tesa9", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트9", "쪽지 내용 테스트"),
        )

        binding.noteRv.adapter = noteListAdapter
        binding.noteRv.addItemDecoration(
            DividerItemDecoration(requireContext(), VERTICAL)
        )
        noteListAdapter.submitList(
            list.toMutableList()
        )
    }
}