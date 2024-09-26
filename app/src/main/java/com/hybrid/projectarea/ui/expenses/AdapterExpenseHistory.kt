package com.hybrid.projectarea.ui.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.domain.model.ExpenseHistory

class AdapterExpenseHistory(private var listaElementos: List<ExpenseHistory>) :
    RecyclerView.Adapter<AdapterExpenseHistory.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val zone = itemView.findViewById<TextView>(R.id.zone)!!
        val expenseType = itemView.findViewById<TextView>(R.id.expenseType)!!
        val amount = itemView.findViewById<TextView>(R.id.amount)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_expense_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.zone.text = listaElementos[position].zone
        holder.expenseType.text = listaElementos[position].expense_type
        holder.amount.text = listaElementos[position].amount
    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}