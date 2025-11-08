package com.musicplayer.presentation.viewModels

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.musicplayer.network.Track
import com.musicplayer.presentation.MusicService.PlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var mediaController: MediaController? = null
    private var progressJob: Job? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0L)
    val progress: StateFlow<Long> = _progress

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val tracks = mutableListOf<Track>()

    init {
        initController(application)
    }

    @OptIn(UnstableApi::class)
    private fun initController(context: Context) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            observeControllerState()
            startProgressUpdates()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun observeControllerState() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                val mediaId = mediaController?.currentMediaItem?.mediaId ?: return
                _currentTrack.value = tracks.find { it.id == mediaId }
            }
        })
    }

    fun setTracks(trackList: List<Track>, startIndex: Int = 0) {
        val controller = mediaController ?: return
        val mediaItems = trackList.map { it.toMediaItem() }
        tracks.clear()
        tracks.addAll(trackList)
        controller.setMediaItems(mediaItems, startIndex, 0L)
        controller.prepare() // ✅ Prepare before play
        controller.play()
    }

    @OptIn(UnstableApi::class)
    fun initPlayer(context: Context) {
        // Only initialize once
        if (!::controllerFuture.isInitialized) {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                observeControllerState()
                startProgressUpdates()
            }, ContextCompat.getMainExecutor(context))
        }
    }

    fun playTrack(context: Context, trackList: List<Track>, startIndex: Int) {
        setTracks(trackList, startIndex)
    }

    fun playPrevious() = mediaController?.seekToPreviousMediaItem()
    fun playNext() = mediaController?.seekToNextMediaItem()
    fun togglePlayPause() {
        mediaController?.run { if (isPlaying) pause() else play() }
    }
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _progress.value = positionMs
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                mediaController?.let { player ->
                    if (player.playbackState != Player.STATE_IDLE &&
                        player.playbackState != Player.STATE_ENDED
                    ) {
                        _progress.value = player.currentPosition
                        _duration.value = player.duration.coerceAtLeast(0L)
                    }
                }
                delay(200)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        MediaController.releaseFuture(controllerFuture)
    }
}

fun Track.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(this.id)
        .setUri(this.audioUrl)
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata.Builder()
                .setTitle(this.title)
                .setArtist(this.artist)
                .setArtworkUri(this.imageUrl?.let { Uri.parse(it) })
                .setAlbumTitle("MusicPlayer Album")
                .build()
        ).build()
}