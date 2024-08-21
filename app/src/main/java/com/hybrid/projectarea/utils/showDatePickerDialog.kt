package com.hybrid.projectarea.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import com.hybrid.projectarea.R
import java.util.Calendar
import java.util.Date

fun showDatePickerDialog(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        ContextThemeWrapper(context, R.style.DatePickerDialogStyle),
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val selectedTime = selectedDate.time
            onDateSelected(selectedTime)
        }, year, month, day)
    val maxDateCalendar = Calendar.getInstance()
    maxDateCalendar.add(Calendar.DAY_OF_MONTH, -2)
    val minDateInMillis = maxDateCalendar.timeInMillis

    datePickerDialog.datePicker.minDate = minDateInMillis
    datePickerDialog.show()
}
