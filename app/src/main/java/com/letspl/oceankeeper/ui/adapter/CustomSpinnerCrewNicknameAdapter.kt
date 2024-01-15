import android.content.Context
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

class CustomSpinnerCrewNicknameAdapter(context: Context, @LayoutRes private val resId: Int, private var menuList: List<MessageModel.MessageSpinnerCrewNicknameItem>, private val messageViewModel: MessageViewModel)
    : ArrayAdapter<MessageModel.MessageSpinnerCrewNicknameItem>(context, resId, menuList) {

    // 드롭다운하지 않은 상태의 Spinner 항목의 뷰
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding = SpinnerOuterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val crewList = messageViewModel.getCrewList()
        Timber.e("messageViewModel.getCrewList() ${messageViewModel.getCrewList()}")
        Timber.e("position $position")
        if(crewList.size > 0) {
            binding.messageTypeTv.text = messageViewModel.getCrewList()[position].nickname
        } else {
            binding.messageTypeTv.text = ""
        }

        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerInnerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val crewList = messageViewModel.getCrewList()
        if(crewList.size > 0) {
            binding.itemTv.text = crewList[position].nickname

            crewList.forEachIndexed { index, messageSpinnerCrewNicknameItem ->
                if (messageSpinnerCrewNicknameItem.isChecked) {
                    binding.checkIcon.visibility = View.VISIBLE
                    binding.spinnerCstl.setBackgroundResource(R.drawable.custom_solid_eceff1)
                } else {
                    binding.checkIcon.visibility = View.GONE
                }
            }
        }

        return binding.root
    }

    override fun getCount() = messageViewModel.getCrewList().size
}