package com.example.smartbandiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbandiot.databinding.ItemChallengeBinding

class ActivitiesHomeAdapter(
    private val dataList: List<ChallengeItemData>
) : RecyclerView.Adapter<ActivitiesHomeAdapter.BindingViewHolder>() {

    inner class BindingViewHolder(val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeItemData) {
            binding.date.text = item.formattedDate
            binding.title.text = item.title
            binding.step.text = item.step.toInt().toString()
            binding.heartrate.text = "${item.heartRate.toInt()} bpm"
            binding.nextDistance.text = "${String.format("%.2f", item.distanceKm)} KM"

            // ubah warna kalau challenge completed
            if (item.title.contains("Completed", ignoreCase = true)) {
                val green = ContextCompat.getColor(binding.root.context, R.color.green_app_theme)
                binding.title.setTextColor(green)
                binding.nextDistance.setTextColor(green)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}
