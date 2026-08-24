package com.deciaventuras.app.data.local.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.local.entity.ChoiceEntity
import com.deciaventuras.app.data.local.entity.DilemmaEntity
import com.deciaventuras.app.data.local.entity.UserProgressEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProgressDaoTest {

    private lateinit var database: DeciAventurasDatabase
    private lateinit var progressDao: UserProgressDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, DeciAventurasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        progressDao = database.userProgressDao()

        runBlocking {
            database.dilemmaDao().insertAll(listOf(DilemmaEntity(1, 0, "D1", "desc", true, false)))
            database.choiceDao().insertAll(
                listOf(
                    ChoiceEntity(1, 1, 0, "texto", "corto", "largo", "rasgo"),
                    ChoiceEntity(2, 1, 1, "texto2", "corto2", "largo2", "rasgo2"),
                )
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_generaIdAutomaticoYQuedaEnObserveAll() = runBlocking {
        val id = progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))

        assertThat(id).isGreaterThan(0L)
        assertThat(progressDao.observeAll().first()).hasSize(1)
    }

    @Test
    fun observeAll_ordenaDelMasRecienteAlMasAntiguo() = runBlocking {
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 2, timestampMillis = 5_000))

        val result = progressDao.observeAll().first()

        assertThat(result.map { it.timestampMillis }).isEqualTo(listOf(5_000L, 1_000L))
    }

    @Test
    fun elMismoDilemaPuedeTenerVariosRegistrosDeProgreso_esHistorialAppendOnly() = runBlocking {
        // Volver a jugar el mismo dilema no debe sobrescribir el registro anterior.
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 2, timestampMillis = 2_000))

        assertThat(progressDao.observeForDilemma(1).first()).hasSize(2)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert_conDilemmaIdInexistente_violaLaClaveForanea() = runBlocking {
        // Caso límite: integridad referencial real, no solo confiada a la UI.
        progressDao.insert(UserProgressEntity(dilemmaId = 999, chosenChoiceId = 1, timestampMillis = 1_000))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert_conChoiceIdInexistente_violaLaClaveForanea() = runBlocking {
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 999, timestampMillis = 1_000))
    }

    @Test
    fun nuevoRegistroDeProgreso_empiezaSinReflexion() = runBlocking {
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))

        val entry = progressDao.observeAll().first().single()

        assertThat(entry.reflection).isNull()
    }

    @Test
    fun updateReflection_adjuntaLaReflexionAUnaEntradaExistente() = runBlocking {
        val id = progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))

        progressDao.updateReflection(id.toInt(), "Aprendí algo importante.")

        val entry = progressDao.observeAll().first().single()
        assertThat(entry.reflection).isEqualTo("Aprendí algo importante.")
    }

    @Test
    fun updateReflection_conIdInexistente_noModificaNadaYNoFalla() = runBlocking {
        // Caso límite: UPDATE sobre un id que no existe no debe lanzar error.
        progressDao.insert(UserProgressEntity(dilemmaId = 1, chosenChoiceId = 1, timestampMillis = 1_000))

        progressDao.updateReflection(9999, "Esto no debería guardarse en ningún lado")

        val entry = progressDao.observeAll().first().single()
        assertThat(entry.reflection).isNull()
    }
}
