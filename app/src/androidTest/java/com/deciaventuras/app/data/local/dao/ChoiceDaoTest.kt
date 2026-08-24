package com.deciaventuras.app.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.local.entity.ChoiceEntity
import com.deciaventuras.app.data.local.entity.DilemmaEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChoiceDaoTest {

    private lateinit var database: DeciAventurasDatabase
    private lateinit var dilemmaDao: DilemmaDao
    private lateinit var choiceDao: ChoiceDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, DeciAventurasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dilemmaDao = database.dilemmaDao()
        choiceDao = database.choiceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun choice(id: Int, dilemmaId: Int, orderIndex: Int) = ChoiceEntity(
        id = id,
        dilemmaId = dilemmaId,
        orderIndex = orderIndex,
        choiceText = "Decisión $id",
        shortTermEffect = "Corto plazo $id",
        longTermEffect = "Largo plazo $id",
        personalityTrait = "Rasgo $id",
    )

    @Test
    fun observeForDilemma_devuelveSoloLasDecisionesDeEseDilemaOrdenadas() = runBlocking {
        dilemmaDao.insertAll(
            listOf(
                DilemmaEntity(1, 0, "D1", "desc", true, false),
                DilemmaEntity(2, 1, "D2", "desc", false, false),
            )
        )
        choiceDao.insertAll(
            listOf(
                choice(id = 12, dilemmaId = 1, orderIndex = 1),
                choice(id = 11, dilemmaId = 1, orderIndex = 0),
                choice(id = 20, dilemmaId = 2, orderIndex = 0),
            )
        )

        val result = choiceDao.observeForDilemma(1).first()

        assertThat(result.map { it.id }).isEqualTo(listOf(11, 12))
    }

    @Test
    fun observeForDilemma_dilemaSinDecisiones_devuelveListaVacia() = runBlocking {
        // Caso límite (Sección 29 del spec maestro): lista vacía.
        val result = choiceDao.observeForDilemma(999).first()
        assertThat(result).isEmpty()
    }

    @Test
    fun borrarUnDilema_borraEnCascadaSusDecisiones() = runBlocking {
        dilemmaDao.insertAll(listOf(DilemmaEntity(1, 0, "D1", "desc", true, false)))
        choiceDao.insertAll(
            listOf(
                choice(id = 1, dilemmaId = 1, orderIndex = 0),
                choice(id = 2, dilemmaId = 1, orderIndex = 1),
            )
        )
        assertThat(choiceDao.count()).isEqualTo(2)

        // No exponemos delete() en el DAO de producción (los dilemas semilla
        // son permanentes); se usa SQL directo para probar la integridad
        // referencial declarada con @ForeignKey(onDelete = CASCADE).
        database.openHelper.writableDatabase.execSQL("DELETE FROM dilemmas WHERE id = 1")

        assertThat(choiceDao.count()).isEqualTo(0)
    }
}
