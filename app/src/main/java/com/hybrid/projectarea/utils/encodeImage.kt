package com.hybrid.projectarea.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun encodeImage(bm: Bitmap): String? {
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 40, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}