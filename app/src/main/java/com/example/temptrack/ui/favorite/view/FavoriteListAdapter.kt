package com.example.temptrack.ui.favorite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.data.model.TempData
import com.example.temptrack.databinding.FavitemBinding

class FavoriteListAdapter(private val clickListener: (TempData) -> Unit, private val deleteListener: (TempData) -> Unit) :
    ListAdapter<TempData, FavoriteListAdapter.FavoriteItemViewHolder>(FavoriteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavitemBinding.inflate(inflater, parent, false)
        return FavoriteItemViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        val tempData = getItem(position)
        holder.bind(tempData)
    }

    inner class FavoriteItemViewHolder(
        private val binding: FavitemBinding,
        private val clickListener: (TempData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TempData) {
            binding.tempData = item
            binding.root.setOnClickListener { clickListener(item) }
            binding.tvMinTemp.text = item.minTemp.toString()
            binding.tvMin.text = item.maxTemp.toString()
            binding.temp.text = item.temp.toString()
            binding.executePendingBindings()
        }
    }

    class FavoriteItemDiffCallback : DiffUtil.ItemCallback<TempData>() {
        override fun areItemsTheSame(oldItem: TempData, newItem: TempData): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: TempData, newItem: TempData): Boolean {
            return oldItem == newItem
        }
    }
}
