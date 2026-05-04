package com.example.metadataguard.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.metadataguard.R

class MediaWatcherService : Service() {

    private lateinit var observer: ContentObserver

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
        startWatching()
        Log.d("ForeServ", "Servicio activado")
    }

    override fun onDestroy() {

        stopForeground(STOP_FOREGROUND_REMOVE)

        contentResolver.unregisterContentObserver(observer)

        Log.d("ForeServ", "Servicio detenido")

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // 🔔 Notificación obligatoria
    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "media_channel")
            .setContentTitle("Vigilando archivos")
            .setContentText("Detectando nuevas fotos y vídeos...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
        Log.d("ForeServ", "Servicio activado")
    }

    // 📡 Crear canal (Android 8+)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "media_channel",
            "Media Watcher",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun startWatching() {
        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                checkLatestMedia()
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }

    // 🔍 Obtener último archivo añadido
    private fun checkLatestMedia() {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATA
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val path = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                )

                processFile(path)
            }
        }
    }

    private fun processFile(path: String) {
        Log.d("MediaWatcher", "Nuevo archivo detectado: $path")

    }
}