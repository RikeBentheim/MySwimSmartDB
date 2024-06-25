package com.example.myswimsmartdb.ui.Composable.components

import android.graphics.ImageFormat
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import androidx.compose.runtime.*
import android.util.Log

@Composable
fun ProcessImageContent() {
    var handlerThread by remember { mutableStateOf<HandlerThread?>(null) }
    var handler by remember { mutableStateOf<Handler?>(null) }
    var imageReader by remember { mutableStateOf<ImageReader?>(null) }

    LaunchedEffect(Unit) {
        handlerThread = HandlerThread("ImageReaderThread").apply { start() }
        handler = Handler(handlerThread!!.looper)

        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.YUV_420_888, 3).apply {
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    try {
                        // Bild verarbeiten
                        Log.d("ImageReader", "Bild wird verarbeitet")
                        // Beispiel: Bildinformationen ausgeben
                        Log.d("ImageReader", "Bildbreite: ${image.width}, Bildhöhe: ${image.height}")
                    } finally {
                        // Bild schließen, um den Puffer freizugeben
                        image.close()
                    }
                } else {
                    Log.w("ImageReader", "Bild ist null, konnte nicht erworben werden")
                }
            }, handler)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            handlerThread?.quitSafely()
            handlerThread = null
            handler = null
            imageReader = null
        }
    }
}
