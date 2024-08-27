package com.hybrid.projectarea.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

object ImageOverlay {
    private val paint = Paint().apply {
        color = Color.WHITE
    }

    fun overlayTextOnImage(imageBitmap: Bitmap, logoBitmap: Bitmap, dateTime: String, latitude: Double, longitude: Double, name:String? = ""): Bitmap {
        val canvas = Canvas(imageBitmap)
//        val logo = Canvas(logoBitmap)

        val textSize = canvas.height * 0.03f
        val logoHeight = canvas.height * 0.08f
//        val logoWidth = (logoHeight / logo.height) * logo.width
        paint.textSize = textSize

//        val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth.toInt(), logoHeight.toInt(), false)

        val datetime = dateTime
        val gps = "Lat: $latitude, Lon: $longitude"

//        val xImage = (canvas.width - scaledLogo.width - 400f)
        val xDate = (canvas.width - paint.measureText(datetime))
        val xGps = (canvas.width - paint.measureText(gps))

//        val yImage = 20f
        val yDate = canvas.height - textSize - 10f
        val yGps = canvas.height - 10f

        if (name !== ""){
            val name = "Ante Proyecto: $name"
            val xName = (canvas.width - paint.measureText(name))
            val yName = canvas.height - 10f
            canvas.drawText(datetime, xDate, yDate - 40f, paint)
            canvas.drawText(gps, xGps, yGps - 40f, paint)
            canvas.drawText(name,xName,yName, paint)
        } else {
            //        canvas.drawBitmap(scaledLogo, xImage, yImage, null)
            canvas.drawText(datetime, xDate, yDate, paint)
            canvas.drawText(gps, xGps, yGps, paint)
        }
        return imageBitmap
    }
}
