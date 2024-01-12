import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer

class MarginItemDecoration(private val marginPx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // 첫 번째 아이템을 제외하고 모든 아이템에 왼쪽 마진 적용
        if (position != 0) {
            outRect.left = marginPx
        }

        // 마지막 아이템을 제외하고 모든 아이템에 오른쪽 마진 적용
        if (position != itemCount - 1) {
            outRect.right = marginPx
        }
    }
}