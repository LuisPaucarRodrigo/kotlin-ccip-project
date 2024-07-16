package com.hybrid.projectarea.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.NameRectifiers

class AdapterRectifiers (private var listaElementos:ArrayList<NameRectifiers>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterRectifiers.ViewHolder>() {
    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val brand = itemView.findViewById<TextView>(R.id.brandRectifiers)!!
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element_project_huawei_rectifiers, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.brand.text= listaElementos[position].brand
    }

    override fun getItemCount(): Int {
       return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
