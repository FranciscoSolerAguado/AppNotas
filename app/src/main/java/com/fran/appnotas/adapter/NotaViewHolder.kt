package com.fran.appnotas.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fran.appnotas.R

class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<TextView>(R.id.tvTitle)
    val content = itemView.findViewById<TextView>(R.id.tvContent)
    val date = itemView.findViewById<TextView>(R.id.tvDate)
}
