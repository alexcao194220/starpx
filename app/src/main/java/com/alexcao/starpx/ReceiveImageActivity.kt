package com.alexcao.starpx

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.alexcao.starpx.ui.theme.StarpxTheme

class ReceiveImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarpxTheme {
                Scaffold {padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
                        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
                            val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                            if (imageUri != null) {
                                val bitmap = getBitmapFromUri(imageUri)
                                bitmapState.value = bitmap
                            }
                        }

                        bitmapState.value?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}