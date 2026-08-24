package com.deciaventuras.app.di

import android.content.Context
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.repository.DilemmaRepositoryImpl
import com.deciaventuras.app.data.repository.UserPreferencesRepositoryImpl
import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import com.deciaventuras.app.domain.usecase.GetJournalUseCase
import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import com.deciaventuras.app.domain.usecase.ResetProgressUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual y liviano: la app es 100% offline y de
 * alcance acotado, así que no se justifica traer Hilt/Dagger (no están en
 * la lista de tecnología de la Sección 21 del spec maestro).
 *
 * Se instancia una única vez desde [com.deciaventuras.app.DeciAventurasApp].
 */
class AppContainer(private val context: Context) {

    /** Alcance de vida de la aplicación: sobrevive a cualquier pantalla individual. */
    val applicationScope = CoroutineScope(SupervisorJob())

    private val database: DeciAventurasDatabase by lazy {
        DeciAventurasDatabase.getInstance(context, applicationScope)
    }

    val dilemmaRepository: DilemmaRepository by lazy {
        DilemmaRepositoryImpl(
            dilemmaDao = database.dilemmaDao(),
            choiceDao = database.choiceDao(),
            userProgressDao = database.userProgressDao(),
        )
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(dao = database.userPreferencesDao())
    }

    val recordChoiceUseCase: RecordChoiceUseCase by lazy {
        RecordChoiceUseCase(dilemmaRepository)
    }

    val getJournalUseCase: GetJournalUseCase by lazy {
        GetJournalUseCase(dilemmaRepository)
    }

    val resetProgressUseCase: ResetProgressUseCase by lazy {
        ResetProgressUseCase(dilemmaRepository, userPreferencesRepository)
    }

    /** Fuerza la apertura de la base de datos (y su precarga) en segundo plano al iniciar la app. */
    fun warmUpDatabase() {
        applicationScope.launch { database.dilemmaDao().count() }
    }
}
