package com.hybrid.projectarea.domain.model

data class ExpenseForm(
    val zone: String,
    val expense_type: String,
    val type_doc: String,
    val ruc: String,
    val doc_number: String,
    val doc_date: String,
    val amount: String,
    val description: String,
    val photo: String,
    val project_id: String,
)

data class ExpenseHistory(
    val zone :String,
    val expense_type: String,
    val amount :String,
    val is_accepted:Int?,
    val description: String
)