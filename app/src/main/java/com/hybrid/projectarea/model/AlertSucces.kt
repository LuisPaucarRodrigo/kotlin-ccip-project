package com.hybrid.projectarea.model

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hybrid.projectarea.databinding.SuccessfulRequestBinding

object Alert {
    fun alertSuccess(context: Context, layoutInflater: LayoutInflater){
        val builder = AlertDialog.Builder(context)
        val alertDialogBinding = SuccessfulRequestBinding.inflate(layoutInflater)
        val dialogView = alertDialogBinding.root
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        },1500)
    }
}