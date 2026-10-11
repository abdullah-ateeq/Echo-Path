package com.example.echopath.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(private val context: Context) {

    @Volatile
    var isEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        }.getOrNull()
    }

    fun vibrateClick() {
        if (!isEnabled) return
        triggerEffect(
            durationMs = 15L,
            amplitude = 50,
            predefinedId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) VibrationEffect.EFFECT_CLICK else null
        )
    }

    fun vibrateRoundStart() {
        if (!isEnabled) return
        triggerEffect(
            durationMs = 25L,
            amplitude = 70,
            predefinedId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) VibrationEffect.EFFECT_TICK else null
        )
    }

    fun vibrateEchoCreated() {
        if (!isEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 25, 40, 45)
            val amplitudes = intArrayOf(0, 80, 0, 110)
            runCatching {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        } else {
            @Suppress("DEPRECATION")
            runCatching { vibrator?.vibrate(70L) }
        }
    }

    fun vibrateCloseCall() {
        if (!isEnabled) return
        triggerEffect(
            durationMs = 12L,
            amplitude = 40,
            predefinedId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) VibrationEffect.EFFECT_TICK else null
        )
    }

    fun vibrateCollision() {
        if (!isEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 60, 40, 120)
            val amplitudes = intArrayOf(0, 200, 0, 255)
            runCatching {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        } else {
            @Suppress("DEPRECATION")
            runCatching { vibrator?.vibrate(220L) }
        }
    }

    private fun triggerEffect(durationMs: Long, amplitude: Int, predefinedId: Int?) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && predefinedId != null) {
                vib.vibrate(VibrationEffect.createPredefined(predefinedId))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(durationMs)
            }
        }
    }
}
