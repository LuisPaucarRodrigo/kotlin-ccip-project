package com.hybrid.projectarea.ui.projecthuawei

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.model.ProjectHuaweiTitle

class AdapterProjectHuaweiTitle (private var listaElementos:ArrayList<ProjectHuaweiTitle>): RecyclerView.Adapter<AdapterProjectHuaweiTitle.ViewHolder>() {
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
        holder.title.text = currentItem.description
        holder.recyclerview.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerview.adapter = AdapterProjectHuaweiCode(currentItem.huawei_project_codes, object : AdapterProjectHuaweiCode.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = currentItem.huawei_project_codes[position]
                if (item.status == 1) {
                    Snackbar.make(holder.itemView, "${item.huawei_code.code} ya está Aprobado", Snackbar.LENGTH_LONG).show()
                }  else if (item.status == 0) {
                    val activity = holder.itemView.context as? AppCompatActivity
                    val conceptFragment = StoreImageHuaweiFragment()

                    val args = Bundle().apply {
                        putString("projectHuaweiCodeId", item.id)
                    }
                    conceptFragment.arguments = args

//                    activity?.supportFragmentManager?.beginTransaction()
//                        ?.replace(R.id.contenedor, conceptFragment)
//                        ?.addToBackStack(null)
//                        ?.commit()
                } else {
                    Snackbar.make(holder.itemView, "Comuniquese con soporte porfavor.", Snackbar.LENGTH_LONG).show()
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