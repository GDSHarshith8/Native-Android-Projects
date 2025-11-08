package com.musicplayer.presentation.MusicService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    private var mediaSession: MediaSession? = null
    private lateinit var playerNotificationManager: PlayerNotificationManager

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // 1️⃣ Setup AudioAttributes for background playback
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )

        // 2️⃣ Create MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .setId("MusicPlayerSession")
            .build()

        // 3️⃣ Setup PlayerNotificationManager
        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return player.currentMediaItem?.mediaMetadata?.title ?: "Unknown"
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return player.currentMediaItem?.mediaMetadata?.artist
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    return PendingIntent.getActivity(
                        this@PlaybackService,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    val imageUrl = player.currentMediaItem?.mediaMetadata?.artworkUri?.toString()
                    if (!imageUrl.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val loader = ImageLoader(this@PlaybackService)
                                val request = ImageRequest.Builder(this@PlaybackService)
                                    .data(imageUrl)
                                    .build()
                                val result = loader.execute(request)
                                if (result is SuccessResult) {
                                    val bitmap = result.drawable.toBitmap()
                                    callback.onBitmap(bitmap)
                                }
                            } catch (_: Exception) {}
                        }
                    }
                    return null
                }
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: android.app.Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) startForeground(notificationId, notification)
                    else stopForeground(false)
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopForeground(true)
                    stopSelf()
                }
            })
            .build()

        // 4️⃣ Attach player to notification
        playerNotificationManager.setPlayer(player)

        // 5️⃣ Optional: control skip actions via MediaSession if needed
        // Media3 handles previous/play/next automatically in notification if your Player supports it
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Stop playback only if the player is not playing or you want to auto-stop when app removed
        if (!player.isPlaying) {
            stopSelf() // triggers onDestroy
        } else {
            // Keep service alive if music is playing in background
            // Optionally, you can move the service to foreground if not already
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null

        playerNotificationManager.setPlayer(null) // detach player
        if (!player.isPlaying) {
            player.stop() // optional
        }

        super.onDestroy()
    }

}