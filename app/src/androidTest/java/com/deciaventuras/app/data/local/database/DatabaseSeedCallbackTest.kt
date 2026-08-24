package com.deciaventuras.app.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifica el comportamiento REAL del `Callback.onCreate` que usa la app en
 * producción (Sección 24 del spec maestro): al abrir la base de datos por
 * primera vez, debe quedar precargada con los 10 dilemas y sus tarjetas de
 * decisión, sin intervención manual.
 *
 * Usa la misma función `DeciAventurasDatabase.seedCallback` que usa
 * `getInstance()` en producción (ver Companion), en vez de duplicar la
 * lógica de precarga dentro del test.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseSeedCallbackTest {

    private lateinit var database: DeciAventurasDatabase
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        if (::database.isInitialized) database.close()
    }

    @Test
    fun primerAperturaDeLaBaseDeDatos_precargaLos10DilemasYSus30Decisiones() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        lateinit var db: DeciAventurasDatabase
        db = Room.inMemoryDatabaseBuilder(context, DeciAventurasDatabase::class.java)
            .addCallback(DeciAventurasDatabase.seedCallback(databaseProvider = { db }, scope = scope))
            .build()
        database = db

        // Disparar la apertura real (onCreate se ejecuta en la primera consulta).
        database.dilemmaDao().count()

        // La precarga corre en una coroutine lanzada desde el callback; se
        // espera de forma acotada (no con un delay fijo arbitrario) a que
        // termine, con timeout para que el test falle rápido si algo rompe.
        withTimeout(5_000) {
            while (database.dilemmaDao().count() < 10) {
                delay(20)
            }
        }

        assertThat(database.dilemmaDao().count()).isEqualTo(10)
        assertThat(database.choiceDao().count()).isEqualTo(30)

        val firstDilemma = database.dilemmaDao().getById(1)
        assertThat(firstDilemma?.isUnlocked).isTrue()
        assertThat(firstDilemma?.title).isEqualTo("El tesoro del recreo")
    }
}
