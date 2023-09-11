package com.letspl.oceankepper.ui.view

import MarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.letspl.oceankepper.data.dto.NoteItemDto
import com.letspl.oceankepper.databinding.FragmentNoteBinding
import com.letspl.oceankepper.ui.adapter.NoteListAdapter
import com.letspl.oceankepper.ui.viewmodel.NoteViewModel

class NoteFragment: Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteListAdapter: NoteListAdapter
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNoteListAdapter()
    }

    private fun setupNoteListAdapter() {
        noteListAdapter = NoteListAdapter(noteViewModel)
        val list = listOf<NoteItemDto>(
            NoteItemDto("asdas", "중현1", "COASTAL", "tesa1", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트1", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현2", "COASTAL", "tesa2", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트2", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현3", "COASTAL", "tesa3", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트3", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현4", "COASTAL", "tesa4", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트4", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현5", "COASTAL", "tesa5", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트5", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현6", "COASTAL", "tesa6", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트6", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현7", "COASTAL", "tesa7", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트7", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현8", "COASTAL", "tesa8", false, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트8", "쪽지 내용 테스트"),
            NoteItemDto("asdas", "중현9", "COASTAL", "tesa9", true, "2023-09-11T07:09:29.903Z", "쪽지 제목 텍스트9", "쪽지 내용 테스트"),
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