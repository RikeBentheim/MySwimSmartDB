package com.example.myswimsmartdb.ui.Composable.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myswimsmartdb.R

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.__posts__1),
        contentDescription = "Hintergrundbild",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
@Preview(showBackground = true)
@Composable
fun BackgroundImagePreview() {
    BackgroundImage()
}