package com.hybrid.projectarea.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

object ImageOverlay {
    private val paint = Paint().apply {
        color = Color.WHITE
    }

    fun overlayTextOnImage(imageBitmap: Bitmap, logoBitmap: Bitmap, dateTime: String, latitude: Double, longitude: Double): Bitmap {
        val canvas = Canvas(imageBitmap)
        val logo = Canvas(logoBitmap)

        val textSize = canvas.height * 0.03f
        val logoHeight = canvas.height * 0.08f
        val logoWidth = (logoHeight / logo.height) * logo.width
        paint.textSize = textSize

        val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth.toInt(), logoHeight.toInt(), false)

        val datetime = dateTime
        val gps = "Lat: $latitude, Lon: $longitude"

        val xImage = (canvas.width - scaledLogo.width - 300f)
        val xDate = (canvas.width - paint.measureText(datetime))
        val xGps = (canvas.width - paint.measureText(gps))

        val yImage = 20f
        val yDate = canvas.height - textSize - 10f
        val yGps = canvas.height - 10f

        canvas.drawBitmap(scaledLogo, xImage, yImage, null)
        canvas.drawText(datetime, xDate, yDate, paint)
        canvas.drawText(gps, xGps, yGps, paint)

        return imageBitmap
    }
}
