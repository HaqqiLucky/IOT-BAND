package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val timestamp: Long = 0,
    val heart_rate: Int = 0,
    val steps: Int = 0
)

class HistoryFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: HistoryAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var historyRef: DatabaseReference
    private var uid: String = ""
    private val listHistory = ArrayList<HistoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_history, container, false)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
        historyRef = database.getReference("history").child(uid)

        btnBack = v.findViewById(R.id.btnBack)
        rvHistory = v.findViewById(R.id.rvHistory)

        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(listHistory)
        rvHistory.adapter = adapter

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        loadHistory()
        return v
    }

    private fun loadHistory() {
        historyRef.orderByChild("timestamp").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listHistory.clear()
                for(item in snapshot.children) {
                    val data = item.getValue(HistoryItem::class.java)
                    data?.let { listHistory.add(it) }
                }
                listHistory.reverse() // newest top
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

class HistoryAdapter(val items: ArrayList<HistoryItem>): RecyclerView.Adapter<HistoryVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_history_run, parent, false)
        return HistoryVH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HistoryVH, position: Int) = holder.bind(items[position])
}

class HistoryVH(v: View): RecyclerView.ViewHolder(v) {

    private val tvDate = v.findViewById<TextView>(R.id.tvDate)
    private val tvHeart = v.findViewById<TextView>(R.id.tvHeart)
    private val tvSteps = v.findViewById<TextView>(R.id.tvSteps)

    fun bind(item: HistoryItem) {
        val sdf = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
        tvDate.text = sdf.format(Date(item.timestamp))
        tvHeart.text = "Heart Rate: ${item.heart_rate} bpm"
        tvSteps.text = "Steps: ${item.steps}"
    }
}
