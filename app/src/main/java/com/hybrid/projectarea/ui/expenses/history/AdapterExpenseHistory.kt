package com.hybrid.projectarea.ui.expenses.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hybrid.projectarea.R
import com.hybrid.projectarea.domain.model.ExpenseHistory

class AdapterExpenseHistory(private var listaElementos: List<ExpenseHistory>) :
    RecyclerView.Adapter<AdapterExpenseHistory.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val component = itemView.findViewById<LinearLayout>(R.id.component)
        val zone = itemView.findViewById<TextView>(R.id.zone)!!
        val expenseType = itemView.findViewById<TextView>(R.id.expenseType)!!
        val amount = itemView.findViewById<TextView>(R.id.amount)!!
        val description = itemView.findViewById<TextView>(R.id.description)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_expense_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaElementos[position]
        holder.zone.text = item.zone
        holder.expenseType.text = item.expense_type
        holder.amount.text = item.amount
        holder.description.text = item.description
        when (item.is_accepted) {
            1 -> holder.component.setBackgroundColor(Color.parseColor("#008000"))
            0 -> holder.component.setBackgroundColor(Color.RED)
        }

    }

    override fun getItemCount(): Int {
        return listaElementos.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}