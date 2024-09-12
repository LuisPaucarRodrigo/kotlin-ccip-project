package com.hybrid.projectarea.view.projecthuawei

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.ProjectHuawei

class AdapterProjectHuawei (private var listaElementos:ArrayList<ProjectHuawei>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterProjectHuawei.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val diu = itemView.findViewById<TextView>(R.id.diu)!!
        val site = itemView.findViewById<TextView>(R.id.site)!!
        val code = itemView.findViewById<TextView>(R.id.code)!!
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element_project_huawei, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.diu.text= listaElementos[position].assigned_diu
        holder.site.text= listaElementos[position].huawei_site.name
        holder.code.text= listaElementos[position].code
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}