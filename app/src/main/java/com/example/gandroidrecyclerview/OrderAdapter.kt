package com.example.gandroidrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.R
import de.hdodenhof.circleimageview.CircleImageView

class OrderAdapter(private var orderList: List<OrderModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PENDING = 0
        private const val VIEW_TYPE_DONE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (orderList[position].status) {
            VIEW_TYPE_DONE
        } else {
            VIEW_TYPE_PENDING
        }
    }

    class PendingOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val name: TextView = itemView.findViewById(R.id.name)
        val status: TextView = itemView.findViewById(R.id.status)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val category: TextView = itemView.findViewById(R.id.category)
        val note: LinearLayout = itemView.findViewById(R.id.note)
    }

    class DoneOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val name: TextView = itemView.findViewById(R.id.name)
        val status: TextView = itemView.findViewById(R.id.status)
        val phone: TextView = itemView.findViewById(R.id.phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_DONE) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_item_done, parent, false)
            DoneOrderViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_item_pending, parent, false)
            PendingOrderViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = orderList[position]

        if (getItemViewType(position) == VIEW_TYPE_DONE) {
            val doneHolder = holder as DoneOrderViewHolder
            doneHolder.name.text = currentItem.name
            doneHolder.status.text = "đã chốt"
            doneHolder.phone.text = currentItem.phone
        } else {
            val pendingHolder = holder as PendingOrderViewHolder
            pendingHolder.name.text = currentItem.name
            pendingHolder.status.text = "đang chốt"
            pendingHolder.phone.text = "${currentItem.phone} / "
            pendingHolder.category.text = currentItem.category
            pendingHolder.note.removeAllViews()
            for (note in currentItem.note) {
                val textView = TextView(pendingHolder.note.context)
                textView.text = "$note  "
                pendingHolder.note.addView(textView)
            }
        }
    }

    override fun getItemCount() = orderList.size

    fun updateData(newList: List<OrderModel>) {
        this.orderList = newList
        notifyDataSetChanged()
    }
}
