package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintLight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PlantPhotoView(
    imageUri: String?,
    imageResName: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loadedBitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUri) {
        if (!imageUri.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(imageUri)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        loadedBitmap = BitmapFactory.decodeStream(stream)
                    }
                } catch (e: Exception) {
                    loadedBitmap = null
                }
            }
        } else {
            loadedBitmap = null
        }
    }

    if (loadedBitmap != null) {
        Image(
            bitmap = loadedBitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        val resId = when (imageResName) {
            "sample_fiddle" -> R.drawable.sample_fiddle
            "sample_snake" -> R.drawable.sample_snake
            "sample_pothos" -> R.drawable.sample_pothos
            "sample_monstera" -> R.drawable.sample_monstera
            else -> R.drawable.sample_monstera
        }

        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
