package com.hybrid.projectarea.view.preproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.CodePhotoPreProject

class AdapterCodePhotoPreProject (private var listaElementos:ArrayList<CodePhotoPreProject>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterCodePhotoPreProject.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val bg = itemView.findViewById<LinearLayout>(R.id.elementcodephoto)!!
        val code = itemView.findViewById<TextView>(R.id.code)!!
        val status = itemView.findViewById<TextView>(R.id.status)!!
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
        holder.code.text= listaElementos[position].code
        holder.status.text= listaElementos[position].status
        when(listaElementos[position].status) {
            "Sin Trabajar" -> holder.bg.setBackgroundColor(Color.LTGRAY)
            "En proceso" -> holder.bg.setBackgroundColor(Color.YELLOW)
            "Aprobado" -> holder.bg.setBackgroundColor(Color.GREEN)
        }
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}