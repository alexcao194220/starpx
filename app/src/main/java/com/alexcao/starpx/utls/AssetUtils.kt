package com.alexcao.starpx.utls

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

suspend fun shareImageFromUrl(context: Context, imageUrl: String) {
    val imageLoader = ImageLoader(context)

    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .allowHardware(false) // Disable hardware bitmaps for drawing to a Canvas
        .build()

    val result = (imageLoader.execute(request) as SuccessResult).drawable
    val bitmap = (result as BitmapDrawable).bitmap
    val uri = saveBitmapToCache(context, bitmap)

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    shareIntent.type = "image/*"

    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val cachePath = File(context.externalCacheDir, "/")
    cachePath.mkdirs()
    val file = File(cachePath, "shared.png")
    val fileOutputStream: FileOutputStream
    try {
        fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    } catch (e: IOException) {
        Log.d("SaveBitmapToCache", "saveBitmapToCache: ${e.message}")
    }
    Log.d("SaveBitmapToCache", "saveBitmapToCache: ${file.absolutePath}")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}