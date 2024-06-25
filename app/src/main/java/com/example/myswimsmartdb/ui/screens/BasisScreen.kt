package com.example.myswimsmartdb.ui.screens

import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.DrawerContent
import com.example.myswimsmartdb.ui.Composable.components.BackgroundImage
import com.example.myswimsmartdb.ui.Composable.components.CustomBottomBar
import com.example.myswimsmartdb.ui.Composable.components.CustomTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasisScreen(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hintergrundbild
            BackgroundImage()

            Scaffold(
                topBar = { CustomTopAppBar(drawerState, scope, "SmartSwimm") },
                bottomBar = { CustomBottomBar() },
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ) { innerPadding ->
                ProcessImageContent()
                content(innerPadding)
            }
        }
    }
}

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

@Preview
@Composable
fun BasisPreview() {
    BasisScreen(navController = rememberNavController()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Preview Content")
        }
    }
}
