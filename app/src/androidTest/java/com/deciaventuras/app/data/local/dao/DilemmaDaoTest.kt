package com.deciaventuras.app.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.local.entity.DilemmaEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DilemmaDaoTest {

    private lateinit var database: DeciAventurasDatabase
    private lateinit var dao: DilemmaDao

    private fun dilemma(id: Int, orderIndex: Int, unlocked: Boolean = false, completed: Boolean = false) =
        DilemmaEntity(
            id = id,
            orderIndex = orderIndex,
            title = "Dilema $id",
            description = "Descripción $id",
            isUnlocked = unlocked,
            isCompleted = completed,
        )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        // Base de datos Room REAL en memoria (no una lista simulada): valida
        // que el esquema, las anotaciones y las queries realmente funcionan.
        database = Room.inMemoryDatabaseBuilder(context, DeciAventurasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.dilemmaDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAllYObserveAll_devuelveOrdenadoPorOrderIndex() = runBlocking {
        dao.insertAll(
            listOf(
                dilemma(id = 3, orderIndex = 2),
                dilemma(id = 1, orderIndex = 0),
                dilemma(id = 2, orderIndex = 1),
            )
        )

        val result = dao.observeAll().first()

        assertThat(result.map { it.id }).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun getById_devuelveNullSiNoExiste() = runBlocking {
        val result = dao.getById(999)
        assertThat(result).isNull()
    }

    @Test
    fun setCompleted_actualizaSoloEseRegistro() = runBlocking {
        dao.insertAll(listOf(dilemma(id = 1, orderIndex = 0), dilemma(id = 2, orderIndex = 1)))

        dao.setCompleted(dilemmaId = 1, isCompleted = true)

        assertThat(dao.getById(1)?.isCompleted).isTrue()
        assertThat(dao.getById(2)?.isCompleted).isFalse()
    }

    @Test
    fun setUnlocked_actualizaSoloEseRegistro() = runBlocking {
        dao.insertAll(listOf(dilemma(id = 1, orderIndex = 0), dilemma(id = 2, orderIndex = 1)))

        dao.setUnlocked(dilemmaId = 2, isUnlocked = true)

        assertThat(dao.getById(1)?.isUnlocked).isFalse()
        assertThat(dao.getById(2)?.isUnlocked).isTrue()
    }

    @Test
    fun count_reflejaElNumeroDeFilasInsertadas() = runBlocking {
        assertThat(dao.count()).isEqualTo(0) // caso límite: base de datos nueva/vacía

        dao.insertAll(listOf(dilemma(id = 1, orderIndex = 0)))

        assertThat(dao.count()).isEqualTo(1)
    }

    @Test
    fun insertAll_conConflicto_reemplazaElRegistroExistente() = runBlocking {
        dao.insertAll(listOf(dilemma(id = 1, orderIndex = 0, unlocked = false)))
        dao.insertAll(listOf(dilemma(id = 1, orderIndex = 0, unlocked = true)))

        assertThat(dao.count()).isEqualTo(1) // caso límite: datos duplicados no crean filas extra
        assertThat(dao.getById(1)?.isUnlocked).isTrue()
    }
}
