package com.example.echopath.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    private var clickTrack: AudioTrack? = null
    private var roundStartTrack: AudioTrack? = null
    private var echoCreatedTrack: AudioTrack? = null
    private var closeCallTrack: AudioTrack? = null
    private var collisionTrack: AudioTrack? = null
    private var cleanLoopTrack: AudioTrack? = null
    private var newBestTrack: AudioTrack? = null

    @Volatile
    var isEnabled: Boolean = true

    init {
        scope.launch {
            initTracks()
        }
    }

    private fun initTracks() {
        runCatching {
            clickTrack = createStaticTrack(generateClick())
            roundStartTrack = createStaticTrack(generateRoundStart())
            echoCreatedTrack = createStaticTrack(generateEchoCreated())
            closeCallTrack = createStaticTrack(generateCloseCall())
            collisionTrack = createStaticTrack(generateCollision())
            cleanLoopTrack = createStaticTrack(generateCleanLoop())
            newBestTrack = createStaticTrack(generateNewBest())
        }
    }

    private fun createStaticTrack(pcm: ShortArray): AudioTrack {
        val byteCount = pcm.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(byteCount)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        return track
    }

    private fun playTrack(track: AudioTrack?) {
        if (!isEnabled || track == null) return
        scope.launch {
            runCatching {
                track.stop()
                track.reloadStaticData()
                track.play()
            }
        }
    }

    fun playClick() = playTrack(clickTrack)
    fun playRoundStart() = playTrack(roundStartTrack)
    fun playEchoCreated() = playTrack(echoCreatedTrack)
    fun playCloseCall() = playTrack(closeCallTrack)
    fun playCollision() = playTrack(collisionTrack)
    fun playCleanLoop() = playTrack(cleanLoopTrack)
    fun playNewBest() = playTrack(newBestTrack)

    fun release() {
        listOf(
            clickTrack,
            roundStartTrack,
            echoCreatedTrack,
            closeCallTrack,
            collisionTrack,
            cleanLoopTrack,
            newBestTrack
        ).forEach {
            runCatching {
                it?.stop()
                it?.release()
            }
        }
    }

    // --- Procedural PCM Generation ---

    private fun generateClick(): ShortArray {
        val durationMs = 25
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 220.0).toFloat()
            val freq = 1100.0 - (i.toFloat() / samples) * 500.0
            val s = sin(2.0 * PI * freq * t).toFloat() * decay
            buffer[i] = (s * Short.MAX_VALUE * 0.45f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateRoundStart(): ShortArray {
        val durationMs = 260
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 12.0).toFloat()
            val s1 = sin(2.0 * PI * 587.33 * t).toFloat() // D5
            val s2 = sin(2.0 * PI * 880.00 * t).toFloat() * 0.5f // A5
            val combined = (s1 + s2) * decay
            buffer[i] = (combined * Short.MAX_VALUE * 0.4f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateEchoCreated(): ShortArray {
        val durationMs = 380
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 7.5).toFloat()
            // Sweeping temporal chord
            val pitchMod = 1.0 + (t / 0.38) * 0.4
            val s1 = sin(2.0 * PI * 440.0 * pitchMod * t).toFloat()
            val s2 = sin(2.0 * PI * 659.25 * pitchMod * t).toFloat() * 0.7f
            val s3 = sin(2.0 * PI * 880.0 * pitchMod * t).toFloat() * 0.4f
            val combined = (s1 + s2 + s3) * decay
            buffer[i] = (combined * Short.MAX_VALUE * 0.35f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateCloseCall(): ShortArray {
        val durationMs = 120
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 24.0).toFloat()
            // Downward airy whoosh
            val freq = 850.0 - (i.toFloat() / samples) * 550.0
            val s = sin(2.0 * PI * freq * t).toFloat() * decay
            buffer[i] = (s * Short.MAX_VALUE * 0.28f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateCollision(): ShortArray {
        val durationMs = 450
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 9.0).toFloat()
            // Temporal collapse: heavy low frequency dropping rapidly + temporal fuzz
            val freq = 180.0 - (i.toFloat() / samples) * 130.0
            val s1 = sin(2.0 * PI * freq * t).toFloat()
            val noise = (Math.random() * 2.0 - 1.0).toFloat() * 0.25f * exp(-t * 30.0).toFloat()
            val combined = (s1 + noise) * decay
            buffer[i] = (combined * Short.MAX_VALUE * 0.55f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateCleanLoop(): ShortArray {
        val durationMs = 320
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 10.0).toFloat()
            val s1 = sin(2.0 * PI * 1046.50 * t).toFloat() // C6
            val s2 = sin(2.0 * PI * 1318.51 * t).toFloat() * 0.5f // E6
            val combined = (s1 + s2) * decay
            buffer[i] = (combined * Short.MAX_VALUE * 0.35f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateNewBest(): ShortArray {
        val durationMs = 550
        val samples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(samples)
        val noteLength = samples / 4
        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
        for (i in 0 until samples) {
            val noteIdx = (i / noteLength).coerceIn(0, 3)
            val noteLocalSample = i % noteLength
            val t = noteLocalSample.toFloat() / sampleRate
            val decay = exp(-t * 14.0).toFloat()
            val s = sin(2.0 * PI * freqs[noteIdx] * t).toFloat() * decay
            buffer[i] = (s * Short.MAX_VALUE * 0.38f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
