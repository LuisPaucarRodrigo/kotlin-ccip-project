package com.hybrid.projectarea.ui.preproject.imageHistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.domain.model.Photo

class AdapterRegisterPhoto (private var listaElementos:List<Photo>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterRegisterPhoto.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val state = itemView.findViewById<TextView>(R.id.state)!!
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.elementregisterphotorecycler, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(listaElementos[position].state ?: "") {
            "" -> {
                holder.state.setTextColor(Color.LTGRAY)
                holder.state.text = "En proceso"
            }
            "0" -> {
                holder.state.setTextColor(Color.RED)
                holder.state.text = "Rechazado"
            }
            "1" -> {
                holder.state.setTextColor(Color.GREEN)
                holder.state.text = "Aprobado"
            }
        }
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}