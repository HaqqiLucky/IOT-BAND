package com.example.smartbandiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbandiot.databinding.ItemChallengeBinding

class ActivitiesHomeAdapter(
    private val dataList: List<HistoryActivityItemData>
) : RecyclerView.Adapter<ActivitiesHomeAdapter.BindingViewHolder>() {

    inner class BindingViewHolder(val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryActivityItemData) {
            binding.date.text = item.formattedDate
            binding.title.text = item.title
            binding.time.text = item.formattedTime
            binding.pace.text = item.formattedPace

            // ✅ Tambahkan event klik pada CardView untuk pindah ke JoggingFragment
            binding.root.setOnClickListener {
                val fragment = JoggingFragment()
                val activity = binding.root.context as MainActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
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

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = dataList.size
}
