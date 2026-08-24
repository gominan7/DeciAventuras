package com.deciaventuras.app.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.local.entity.UserPreferencesEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserPreferencesDaoTest {

    private lateinit var database: DeciAventurasDatabase
    private lateinit var dao: UserPreferencesDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, DeciAventurasDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.userPreferencesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observe_sinRegistroGuardado_emiteNull() = runBlocking {
        // Caso límite: primera apertura de la app, tabla vacía.
        assertThat(dao.observe().first()).isNull()
    }

    @Test
    fun upsert_luegoObserve_devuelveElRegistroGuardado() = runBlocking {
        dao.upsert(
            UserPreferencesEntity(
                alias = "Sofía",
                avatarIndex = 2,
                hapticsEnabled = false,
                onboardingCompleted = true,
            )
        )

        val stored = dao.observe().first()

        assertThat(stored?.alias).isEqualTo("Sofía")
        assertThat(stored?.avatarIndex).isEqualTo(2)
        assertThat(stored?.hapticsEnabled).isFalse()
        assertThat(stored?.onboardingCompleted).isTrue()
    }

    @Test
    fun upsert_esSiempreUnaSolaFila_nuncaCreaPerfilesDuplicados() = runBlocking {
        dao.upsert(UserPreferencesEntity(alias = "Primero", avatarIndex = 0, hapticsEnabled = true, onboardingCompleted = false))
        dao.upsert(UserPreferencesEntity(alias = "Segundo", avatarIndex = 1, hapticsEnabled = true, onboardingCompleted = true))

        val stored = dao.observe().first()

        assertThat(stored?.alias).isEqualTo("Segundo")
        assertThat(stored?.id).isEqualTo(UserPreferencesEntity.SINGLETON_ID)
    }

    @Test
    fun clear_borraElPerfilGuardado() = runBlocking {
        dao.upsert(UserPreferencesEntity(alias = "Alguien", avatarIndex = 0, hapticsEnabled = true, onboardingCompleted = true))

        dao.clear()

        assertThat(dao.observe().first()).isNull()
    }
}
