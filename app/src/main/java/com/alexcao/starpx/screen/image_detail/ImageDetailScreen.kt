package com.alexcao.starpx.screen.image_detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.alexcao.starpx.utls.shareImageFromUrl
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun ImageDetailScreen(
    url: String,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            Button(onClick = {
                coroutineScope.launch {
                    shareImageFromUrl(context, url)
                }
            }) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding),
            contentAlignment = Alignment.Center
        ) {
            var isLoading by remember { mutableStateOf(true) }

            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = url,
                contentDescription = null,
                imageLoader = ImageLoader(context),
                onSuccess = {
                    isLoading = false
                },
            )

            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}