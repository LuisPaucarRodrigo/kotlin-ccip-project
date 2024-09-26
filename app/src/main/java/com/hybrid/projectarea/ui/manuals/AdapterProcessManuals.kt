package com.hybrid.projectarea.ui.manuals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.domain.model.GetProcessManuals

class AdapterProcessManuals (private var listaElementos:List<GetProcessManuals>, private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterProcessManuals.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val imageType = itemView.findViewById<ImageView>(R.id.imageType)!!
        val name = itemView.findViewById<TextView>(R.id.name)!!
        val size = itemView.findViewById<TextView>(R.id.size)!!
        val viewSize = itemView.findViewById<TextView>(R.id.viewSize)
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element_process_manuals, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elemento = listaElementos[position]

        holder.name.text = elemento.name
        holder.size.text = elemento.size
        when (elemento.type) {
            "folder" -> {
                holder.imageType.setImageResource(R.drawable.baseline_folder_24)
                holder.viewSize.isVisible = false
            }
            "archive" -> {
                holder.imageType.setImageResource(R.drawable.baseline_insert_drive_file_24)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}