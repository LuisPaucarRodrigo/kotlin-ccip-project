package com.hybrid.projectarea.view.checklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.ChecklistHistory

class AdapterChecklistHistory (private var listaElementos:ArrayList<ChecklistHistory>): RecyclerView.Adapter<AdapterChecklistHistory.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val type = itemView.findViewById<TextView>(R.id.type)!!
        val date = itemView.findViewById<TextView>(R.id.date)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element_checklist_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.type.text= listaElementos[position].type
        holder.date.text= listaElementos[position].created_at
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}