package com.dicoding.picodiploma.feedeepaplikasi.Home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64String():String{
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG,10,this)
        return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
    }
}

fun base64ToBitmap(base64: String): Bitmap {
    val imageByte = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageByte,0,imageByte.size)
}