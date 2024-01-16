import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.model.MessageModel
import com.letspl.oceankeeper.databinding.SpinnerInnerLayoutBinding
import com.letspl.oceankeeper.databinding.SpinnerOuterLayoutBinding
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel
import timber.log.Timber

class CustomSpinnerCrewNicknameAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private var menuList: List<MessageModel.MessageSpinnerCrewNicknameItem>,
    private val messageViewModel: MessageViewModel
) : ArrayAdapter<MessageModel.MessageSpinnerCrewNicknameItem>(context, resId, menuList) {

    // 드롭다운하지 않은 상태의 Spinner 항목의 뷰
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding =
            SpinnerOuterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.messageTypeTv.text = if (messageViewModel.getReceiveList().isNotEmpty()) {
            messageViewModel.getReceiveListStr()
        } else {
            menuList[position].nickname
        }

        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            SpinnerInnerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.itemTv.text = menuList[position].nickname
        binding.checkIcon.visibility = if (menuList[position].isChecked) {
            binding.spinnerCstl.setBackgroundColor(Color.parseColor("#eceff1"))
            View.VISIBLE
        } else {
            binding.spinnerCstl.setBackgroundColor(Color.parseColor("#ffffff"))
            View.GONE
        }

        binding.spinnerCstl.setOnClickListener {
            menuList[position].isChecked = !menuList[position].isChecked

            if (menuList[position].isChecked) {
                binding.checkIcon.visibility = View.VISIBLE
                binding.spinnerCstl.setBackgroundColor(Color.parseColor("#eceff1"))
                messageViewModel.addReceiveList(menuList[position].nickname)
            } else {
                binding.checkIcon.visibility = View.GONE
                binding.spinnerCstl.setBackgroundColor(Color.parseColor("#ffffff"))
                messageViewModel.removeReceiveList(menuList[position].nickname)
            }

            notifyDataSetChanged()
        }

        return binding.root
    }

    override fun getCount() = menuList.size

    fun setMenuList(list: List<MessageModel.MessageSpinnerCrewNicknameItem>) {
        menuList = list
        notifyDataSetChanged()
    }
}