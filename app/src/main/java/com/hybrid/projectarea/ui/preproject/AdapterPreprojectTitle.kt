package com.hybrid.projectarea.ui.preproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.domain.model.PreprojectTitle


class AdapterPreprojectTitle (private var listaElementos:List<PreprojectTitle>): RecyclerView.Adapter<AdapterPreprojectTitle.ViewHolder>() {

    inner class ViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)!!
        val recyclerview = itemView.findViewById<RecyclerView>(R.id.recyclerviewtitle)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element_title_preproject, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listaElementos[position]
        holder.title.text = currentItem.type
        holder.recyclerview.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerview.adapter = AdapterCodePhotoPreProject(currentItem.preproject_codes, object : AdapterCodePhotoPreProject.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = currentItem.preproject_codes[position]
                if (item.status == "Aprobado") {
                    Snackbar.make(holder.itemView, "${item.code.code} ya está Aprobado", Snackbar.LENGTH_LONG).show()
                } else {
                    val activity = holder.itemView.context as? AppCompatActivity

                    val args = Bundle().apply {
                        putString("code_id", item.id)
                    }
                    activity?.findNavController(R.id.container)?.navigate(R.id.to_PreProjectEspecificFragment,args)
//                    conceptFragment.arguments = args
//
//                    activity?.supportFragmentManager?.beginTransaction()
//                        ?.replace(R.id.contenedor, conceptFragment)
//                        ?.addToBackStack(null)
//                        ?.commit()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}