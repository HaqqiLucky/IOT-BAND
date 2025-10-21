package com.example.smartbandiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbandiot.databinding.ItemChallengeBinding
//import com.example.smartbandiot.databinding.ItemHistoryBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ActivitiesHomeAdapter (private val dataList: List<HistoryActivityItemData>):
    RecyclerView.Adapter<ActivitiesHomeAdapter.BindingViewHolder>(){

    inner class BindingViewHolder(private val binding: ItemChallengeBinding) :
            RecyclerView.ViewHolder(binding.root){
            fun bind(item: HistoryActivityItemData){
                binding.date.text = item.formattedDate
                binding.title.text = item.title
                binding.time.text = item.formattedTime
                binding.pace.text = item.formattedPace
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = ItemChallengeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BindingViewHolder(binding)
    }
    // 3. onBindViewHolder: Menghubungkan data dengan ViewHolder
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    // 4. getItemCount: Mengembalikan jumlah total item
    override fun getItemCount(): Int {
        return dataList.size
    }
}