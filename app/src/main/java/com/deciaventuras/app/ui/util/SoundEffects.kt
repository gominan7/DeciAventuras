package com.deciaventuras.app.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.deciaventuras.app.R

/**
 * Reproductor liviano de los efectos de sonido de DeciAventuras (SoundPool,
 * pensado para sonidos cortos de baja latencia — no MediaPlayer). Los
 * archivos en `res/raw/` están sintetizados matemáticamente con
 * `scripts/gen_sound_effects.py`, no son assets bajados de terceros
 * (Sección 23 del spec maestro: 100% offline, sin dependencias externas).
 */
class SoundEffects(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val successSoundId = soundPool.load(context, R.raw.sfx_success, 1)
    private val celebrationSoundId = soundPool.load(context, R.raw.sfx_celebration, 1)

    /** Al soltar una tarjeta con éxito sobre la Brújula. */
    fun playSuccess() {
        soundPool.play(successSoundId, 1f, 1f, 1, 0, 1f)
    }

    /** Al completar el último dilema disponible (Pantalla de Celebración). */
    fun playCelebration() {
        soundPool.play(celebrationSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

/**
 * Crea un único [SoundEffects] por composición y lo libera automáticamente
 * al salir de pantalla, para no fugar el `SoundPool` (recurso nativo).
 */
@Composable
fun rememberSoundEffects(): SoundEffects {
    val context = LocalContext.current
    val soundEffects = remember { SoundEffects(context.applicationContext) }
    DisposableEffect(Unit) {
        onDispose { soundEffects.release() }
    }
    return soundEffects
}
