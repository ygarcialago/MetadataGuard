package com.example.metadataguard.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.metadataguard.ui.theme.MetadataGuardTheme

@Composable
fun MainWindow (){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("hola mundo")
    }
}

@Preview(showBackground = true)
@Composable
fun MainWindowPreview (){
    MetadataGuardTheme() {
        MainWindow()
    }
}