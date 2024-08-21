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
        val site = itemView.findViewById<TextView>(R.id.site)!!
        val elaborated = itemView.findViewById<TextView>(R.id.elaborated)!!
        val code = itemView.findViewById<TextView>(R.id.code)!!
        val name = itemView.findViewById<TextView>(R.id.name)!!
        val address = itemView.findViewById<TextView>(R.id.address)!!
        val reference = itemView.findViewById<TextView>(R.id.reference)!!
        val access = itemView.findViewById<TextView>(R.id.access)!!
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
        holder.site.text= listaElementos[position].site
        holder.elaborated.text= listaElementos[position].elaborated
        holder.code.text= listaElementos[position].code
        holder.name.text= listaElementos[position].name
        holder.address.text= listaElementos[position].address
        holder.reference.text= listaElementos[position].reference
        holder.access.text= listaElementos[position].access
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}