package com.hybrid.projectarea.view.preproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.ElementPreProjectRecyclerView

class AdapterPreProjectElement (private var listaElementos:List<ElementPreProjectRecyclerView>,private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterPreProjectElement.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val code = itemView.findViewById<TextView>(R.id.code)!!
        val description = itemView.findViewById<TextView>(R.id.description)!!
        val observation = itemView.findViewById<TextView>(R.id.observation)!!
        val datevisit = itemView.findViewById<TextView>(R.id.datevisit)!!
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.elementpreprojectrecycler, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.code.text= listaElementos[position].code
        holder.description.text= listaElementos[position].description
        holder.observation.text= listaElementos[position].observation
        holder.datevisit.text= listaElementos[position].date
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}