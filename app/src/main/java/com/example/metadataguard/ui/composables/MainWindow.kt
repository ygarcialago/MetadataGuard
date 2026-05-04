package com.example.metadataguard.ui.composables

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.metadataguard.services.MediaWatcherService
import com.example.metadataguard.ui.theme.MetadataGuardTheme

@Composable
fun MainWindow() {

    RequestPermissionsOnStart()

    val context = LocalContext.current

    var isServiceRunning by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "MetadataGuard",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {

                val intent = Intent(
                    context,
                    MediaWatcherService::class.java
                )

                if (!isServiceRunning) {

                    ContextCompat.startForegroundService(
                        context,
                        intent
                    )

                    isServiceRunning = true

                } else {

                    context.stopService(intent)

                    isServiceRunning = false
                }
            }
        ) {

            Text(
                if (isServiceRunning)
                    "Desactivar servicio"
                else
                    "Activar servicio"
            )
        }
    }
}

@Composable
fun RequestPermissionsOnStart() {
    val context = LocalContext.current

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    var alreadyRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        results.forEach { (perm, granted) ->
            println("$perm granted: $granted")
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted && !alreadyRequested) {
            launcher.launch(permissions)
            alreadyRequested = true
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainWindowPreview (){
    MetadataGuardTheme() {
        MainWindow()
    }
}