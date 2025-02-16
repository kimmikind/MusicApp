package com.example.musicapp.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import android.app.Service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.NotificationUtil.createNotificationChannel

class MusicService : Service() {


    companion object {
        const val CHANNEL_ID = "music_channel"
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_UPDATE_PROGRESS = "update_progress"
        const val ACTION_SEEK = "action_seek"
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationManager: NotificationManager
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition.toLong()
                val duration = mediaPlayer.duration.toLong()
                updateNotification("Playing", currentPosition, duration)
                sendBroadcast(Intent(ACTION_UPDATE_PROGRESS).apply {
                    putExtra("currentPosition", currentPosition)
                    setPackage(packageName)
                })
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicService", "Сервис создан")
        mediaPlayer = MediaPlayer()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicService", "onStartCommand вызван с action: ${intent?.action}")
        when (intent?.action) {
            ACTION_PLAY -> {
                Log.d("MusicService", "Получена команда PLAY")
                play()
            }
            ACTION_PAUSE -> {
                Log.d("MusicService", "Получена команда PAUSE")
                pause()
            }
            ACTION_NEXT -> {
                Log.d("MusicService", "Получена команда NEXT")
                nextTrack()
            }
            ACTION_PREVIOUS -> {
                Log.d("MusicService", "Получена команда PREVIOUS")
                previousTrack()
            }
            ACTION_SEEK -> {
                val position = intent.getLongExtra("position", 0L)
                seekTo(position)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun play() {
        Log.d("MusicService", "play() вызван")
        mediaPlayer.start()
        updateNotification("Playing", mediaPlayer.currentPosition.toLong(), mediaPlayer.duration.toLong())
        startProgressUpdates()
        sendBroadcast(Intent(ACTION_PLAY).apply {
            setPackage(packageName) // Указываем пакет приложения
        })
    }

    private fun pause() {
        Log.d("MusicService", "pause() вызван")
        mediaPlayer.pause()
        updateNotification("Paused", mediaPlayer.currentPosition.toLong(), mediaPlayer.duration.toLong())
        stopProgressUpdates()
        sendBroadcast(Intent(ACTION_PAUSE).apply {
            setPackage(packageName) // Указываем пакет приложения
        })
    }
    private fun seekTo(position: Long) {
        Log.d("MusicService", "seekTo вызван, позиция: $position")
        mediaPlayer.seekTo(position.toInt())
        sendBroadcast(Intent(ACTION_UPDATE_PROGRESS).apply {
            putExtra("currentPosition", position)
            setPackage(packageName)
        })
    }
    private fun startProgressUpdates() {
        handler.post(updateProgressRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(updateProgressRunnable)
    }

    private fun nextTrack() {
        // Логика переключения на следующий трек
        updateNotification("Playing next track")
    }

    private fun previousTrack() {
        // Логика переключения на предыдущий трек
        updateNotification("Playing previous track")
    }


    private fun updateNotification(status: String, currentPosition: Long = 0L, duration: Long = 0L) {
        Log.d("MusicService", "Обновление уведомления: $status")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PLAY }
        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PAUSE }
        val pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, MusicService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)

        val previousIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PREVIOUS }
        val previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)

        val mediaSession = MediaSessionCompat(this, "MusicService").apply {
            isActive = true
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Важно для фонового воспроизведения
            .setOngoing(true) // Уведомление нельзя смахнуть
            .setOnlyAlertOnce(true) // Без звука при обновлении
            .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.ic_play, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3)
                .setMediaSession(mediaSession.sessionToken)) // Добавь MediaSession, если есть
            .setProgress(duration.toInt(), currentPosition.toInt(), false) // Добавляем прогресс
            .build()

        startForeground(1, notification)
        Log.d("MusicService", "Уведомление запущено в Foreground")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d("MusicService", "Канал уведомлений создан")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
