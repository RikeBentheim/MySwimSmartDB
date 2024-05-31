package com.example.myswimsmartdb.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.ui.theme.Platinum


@Composable
fun MainContent(modifier: Modifier = Modifier, innerPadding: PaddingValues = PaddingValues()) {
    Column(modifier = Modifier.padding(innerPadding)) {
        Image(
            painter = painterResource(id = R.drawable.adobestock_288862937),
            contentDescription = "Header",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(R.string.schwimmverein_haltern),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(12.dp),
            color = Platinum
        )
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(id = R.drawable.unterricht),
            contentDescription = "Unterricht",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(250.dp)
                .border(4.dp, SkyBlue),
            contentScale = ContentScale.Crop
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MainContent()
}