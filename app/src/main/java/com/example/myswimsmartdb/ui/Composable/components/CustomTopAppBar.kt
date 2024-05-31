package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.Platinum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(drawerState: DrawerState, scope: CoroutineScope, title: String) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = IndigoDye,
            titleContentColor = Platinum
        ),
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Menü", tint = Platinum)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CustomTopAppBarPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    CustomTopAppBar(drawerState = drawerState, scope = scope, title = "Preview Title")
}
