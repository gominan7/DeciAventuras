package com.deciaventuras.app

import android.app.Application
import com.deciaventuras.app.di.AppContainer

/**
 * Punto de entrada de la aplicación. Expone el [AppContainer] (base de datos
 * Room + repositorio + casos de uso) para que las ViewModels lo consuman al
 * construirse, sin ningún framework externo y sin ningún servicio remoto
 * (Sección 23 del spec maestro: 100% offline).
 */
class DeciAventurasApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.warmUpDatabase()
    }
}
