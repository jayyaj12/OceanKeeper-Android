import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.databinding.SpinnerInnerLayoutBinding
import com.letspl.oceankepper.databinding.SpinnerOuterLayoutBinding
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel

class CustomSpinnerProjectNameAdapter(context: Context, @LayoutRes private val resId: Int, private val menuList: List<MessageModel.MessageSpinnerProjectNameItem>, private val messageViewModel: MessageViewModel)
    : ArrayAdapter<MessageModel.MessageSpinnerProjectNameItem>(context, resId, menuList) {

    // 드롭다운하지 않은 상태의 Spinner 항목의 뷰
    override fun getView(position: Int, converView: View?, parent: ViewGroup): View {
        val binding = SpinnerOuterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.messageTypeTv.text = menuList[position].title

        return binding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerInnerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.itemTv.text = menuList[position].title

        if(messageViewModel.getActivityNameSpinnerClickPos() == position) {
            binding.checkIcon.visibility = View.VISIBLE
            binding.spinnerCstl.setBackgroundResource(R.drawable.custom_solid_eceff1)
        } else {
            binding.checkIcon.visibility = View.GONE
        }

        return binding.root
    }

    override fun getCount() = menuList.size
}