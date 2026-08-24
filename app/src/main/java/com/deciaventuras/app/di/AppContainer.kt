package com.deciaventuras.app.di

import android.content.Context
import com.deciaventuras.app.data.local.database.DeciAventurasDatabase
import com.deciaventuras.app.data.repository.DilemmaRepositoryImpl
import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.usecase.GetJournalUseCase
import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual y liviano: la app es 100% offline y de
 * alcance acotado (3 pantallas), así que no se justifica traer Hilt/Dagger
 * (no están en la lista de tecnología de la Sección 21 del spec maestro).
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

    val recordChoiceUseCase: RecordChoiceUseCase by lazy {
        RecordChoiceUseCase(dilemmaRepository)
    }

    val getJournalUseCase: GetJournalUseCase by lazy {
        GetJournalUseCase(dilemmaRepository)
    }

    /** Fuerza la apertura de la base de datos (y su precarga) en segundo plano al iniciar la app. */
    fun warmUpDatabase() {
        applicationScope.launch { database.dilemmaDao().count() }
    }
}
