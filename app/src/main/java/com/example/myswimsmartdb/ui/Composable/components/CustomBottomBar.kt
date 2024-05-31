package com.example.myswimsmartdb.ui.Composable.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.Platinum

@Composable
fun CustomBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(IndigoDye)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = Icons.Default.Home, contentDescription = "Facebook", tint = Platinum)
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(R.drawable.facebook),
            contentDescription = "Facebook",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(R.drawable.insta),
            contentDescription = "Instagram",
            modifier = Modifier.size(24.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun CustomBottomBarPreview() {
    CustomBottomBar()
}