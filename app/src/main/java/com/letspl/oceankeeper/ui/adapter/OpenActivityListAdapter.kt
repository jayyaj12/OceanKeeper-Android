package com.letspl.oceankeeper.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.letspl.oceankeeper.data.dto.GetUserActivityListDto
import com.letspl.oceankeeper.databinding.ItemOpenActivityBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class OpenActivityListAdapter(
    private val context: Context,
    private val onClickedItem: (String) -> Unit,
    private val onClickManage: (String, GetUserActivityListDto) -> Unit,
    private val onClickEditRecruit: (String) -> Unit,
    private val onClickCancelRecruit: (String) -> Unit,
) : ListAdapter<GetUserActivityListDto, OpenActivityListAdapter.OpenActivityViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GetUserActivityListDto>() {
            override fun areItemsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetUserActivityListDto, newItem: GetUserActivityListDto,
            ): Boolean {
                return oldItem.activityId == newItem.activityId
            }
        }
    }

    inner class OpenActivityViewHolder(private val binding: ItemOpenActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: GetUserActivityListDto) {
            Glide.with(context).load(item.activityImageUrl).into(binding.thumbnailIv)
            binding.titleTv.text = item.title
            binding.applyCountTv.text = "${item.participants}/${item.quota}"
            binding.hostTv.text = "모집키퍼: ${item.hostNickname}"
            binding.nowApplyCountTv.text = "신청현황: ${
                if (item.participants < 10) {
                    "0${item.participants}"
                } else {
                    item.participants
                }
            }/${item.quota}명"
            binding.placeTv.text = "활동장소: ${item.address}"
            binding.dateTv.text = "모집기간: ${
                item.recruitStartAt.substring(2, item.recruitStartAt.length).replace("-", ".")
            }~${item.recruitEndAt.substring(2, item.recruitEndAt.length).replace("-", ".")}"
            binding.timeTv.text = "활동시작: ${item.startAt.substring(2, 10).replace("-", ".")} ${
                item.startAt.substring(11, 13)
            }시"

            showBtn(item.status, item.startAt)
            onClickBtn(item)

            binding.topTv.setOnClickListener {
                onClickedItem(item.activityId)
            }
        }

        // 클릭 이벤트
        private fun onClickBtn(item: GetUserActivityListDto) {
            // 신청서 관리
            binding.manageApplyTv.setOnClickListener {
                onClickManage(item.activityId, item)
            }
            // 모집 수정하기
            binding.editRecruitActivityTv.setOnClickListener {
                onClickEditRecruit(item.activityId)
            }
            // 모집 취소
            binding.cancelRecruitTv.setOnClickListener {
                onClickCancelRecruit(item.activityId)
            }
        }

        // 활동별에 맞는 버튼을 표시함
        private fun showBtn(status: String, startAt: String) {
            Timber.e("status $status")
            when (status) {
                "OPEN" -> {
                    binding.manageApplyTv.visibility = View.VISIBLE
                    binding.editRecruitActivityTv.visibility = View.VISIBLE
                    binding.cancelRecruitTv.visibility = View.VISIBLE
                }
                // 활동 종료
                "CLOSED" -> {
                    binding.applyCountTv.text = "활동 종료"
                    binding.editRecruitActivityTv.visibility = View.GONE
                    binding.cancelRecruitTv.visibility = View.GONE
                    binding.countIv.visibility = View.GONE
                    binding.manageApplyTv.visibility = if (isBecomeEndAfter7(startAt)) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
                // 모집 취소
                "CANCEL" -> {
                    binding.countIv.visibility = View.GONE
                    binding.applyCountTv.text = "모집 취소"
                }
                // 모집 종료
                "RECRUITMENT_CLOSE" -> {
                    binding.countIv.visibility = View.GONE
                    binding.applyCountTv.text = "모집 종료"
                    binding.manageApplyTv.visibility = View.VISIBLE
                }
            }

        }

        // 활동 종료일 이후 7일까지는 신청자 관리 버튼 표시되어야 함
        private fun isBecomeEndAfter7(startAt: String): Boolean {
            val today = Calendar.getInstance()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startAt)

            val diffDate = (today.time.time - date.time) / (60 * 60 * 24 * 1000)
            return diffDate > 7
        }

        // 마지막 뷰의 경우 하단에 10dp에 해당하는 여백을 가진다.
        fun showLastView() {
            binding.lastV.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenActivityViewHolder {
        return OpenActivityViewHolder(ItemOpenActivityBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: OpenActivityViewHolder, position: Int) {
        holder.onBind(currentList[position])

        if (position == currentList.size - 1) {
            holder.showLastView()
        }
    }
}