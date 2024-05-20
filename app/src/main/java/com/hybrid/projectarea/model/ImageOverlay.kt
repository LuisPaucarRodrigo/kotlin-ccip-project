package com.hybrid.projectarea.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

object ImageOverlay {
    private val paint = Paint().apply {
        color = Color.WHITE
    }

    fun overlayTextOnImage(imageBitmap: Bitmap,logoBitmap: Bitmap, dateTime: String, latitude: Double, longitude: Double): Bitmap {
        val canvas = Canvas(imageBitmap)

        val textSize = (canvas.height * 0.025f)
        paint.textSize = textSize

        val datetime = dateTime
        val gps = "Lat: $latitude, Lon: $longitude "
        val ximage = (canvas.width - 230f)
        val xdate = (canvas.width - paint.measureText(datetime))
        val xgps = (canvas.width - paint.measureText(gps))
        val yimage = 20f
        val ydate = canvas.height - textSize - 10f
        val ygps = canvas.height - 10f

        canvas.drawBitmap(logoBitmap, ximage, yimage, null)
        canvas.drawText(datetime, xdate, ydate, paint)
        canvas.drawText(gps, xgps, ygps, paint)

        return imageBitmap
    }
}
