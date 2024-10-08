package com.hybrid.projectarea.view.projecthuawei

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.CodePhotoPreProject
import com.hybrid.projectarea.model.ProjectHuaweiCode

class AdapterProjectHuaweiCode (private var listaElementos:List<ProjectHuaweiCode>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterProjectHuaweiCode.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val bg = itemView.findViewById<LinearLayout>(R.id.elementcodephoto)!!
        val code = itemView.findViewById<TextView>(R.id.code)!!
        val status = itemView.findViewById<TextView>(R.id.status)!!
        val rejected = itemView.findViewById<TextView>(R.id.rejected)!!
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.elementcodepreprojectrecycler, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.code.text= listaElementos[position].huawei_code.code
        if (listaElementos[position].status == 1){
            holder.status.text = "Aprobado"
            holder.bg.setBackgroundColor(Color.GREEN)
        }else{
            when(listaElementos[position].state) {
                0 -> {
                    holder.bg.setBackgroundColor(Color.LTGRAY)
                    holder.status.text = "Pendiente"
                }
                1 -> {
                    holder.bg.setBackgroundColor(Color.YELLOW)
                    holder.status.text = "En Proceso"
                }
            }
        }

        if (listaElementos[position].rejected_quantity > 0){
            holder.rejected.apply {
                isVisible = true
                setTextColor(Color.RED)
            }
            holder.rejected.text = listaElementos[position].rejected_quantity.toString()
        }
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}