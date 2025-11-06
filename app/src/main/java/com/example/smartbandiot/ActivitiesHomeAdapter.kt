package com.example.smartbandiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartbandiot.databinding.ItemChallengeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ActivitiesHomeAdapter(
    private val dataList: List<HistoryActivityItemData>,
    private val onChallengeFinished: (() -> Unit)? = null
) : RecyclerView.Adapter<ActivitiesHomeAdapter.BindingViewHolder>() {

    inner class BindingViewHolder(val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryActivityItemData) {
            binding.date.text = item.formattedDate
            binding.title.text = item.title
            binding.time.text = item.formattedTime
            binding.pace.text = item.formattedPace
            binding.totalDistance.text = "${item.distanceKm} KM"

            // ketika user klik challenge → user mau menjalankan challenge
            binding.root.setOnClickListener {
                // masuk ke jogging fragment
                val fragment = JoggingFragment()
                val activity = binding.root.context as MainActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit()

                // saat challenge considered DONE (masuk running)
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users").child(uid).child("today_challenge").removeValue()

                onChallengeFinished?.invoke()
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
