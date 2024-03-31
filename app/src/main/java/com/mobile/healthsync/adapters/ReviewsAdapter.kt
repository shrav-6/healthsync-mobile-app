import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Reviews

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    private var reviewsList: List<Reviews> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentItem = reviewsList[position]
        holder.textComment.text = currentItem.comment
        holder.ratingBar.rating = currentItem.stars.toFloat()
    }

    override fun getItemCount() = reviewsList.size

    fun setReviews(reviews: List<Reviews>) {
        reviewsList = reviews
        notifyDataSetChanged()
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textComment: TextView = itemView.findViewById(R.id.textComment)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}
