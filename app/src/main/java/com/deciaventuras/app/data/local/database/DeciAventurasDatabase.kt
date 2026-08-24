package com.deciaventuras.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deciaventuras.app.data.local.dao.ChoiceDao
import com.deciaventuras.app.data.local.dao.DilemmaDao
import com.deciaventuras.app.data.local.dao.UserPreferencesDao
import com.deciaventuras.app.data.local.dao.UserProgressDao
import com.deciaventuras.app.data.local.entity.ChoiceEntity
import com.deciaventuras.app.data.local.entity.DilemmaEntity
import com.deciaventuras.app.data.local.entity.UserPreferencesEntity
import com.deciaventuras.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "deciaventuras.db"

@Database(
    entities = [
        DilemmaEntity::class,
        ChoiceEntity::class,
        UserProgressEntity::class,
        UserPreferencesEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class DeciAventurasDatabase : RoomDatabase() {

    abstract fun dilemmaDao(): DilemmaDao
    abstract fun choiceDao(): ChoiceDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        @Volatile
        private var instance: DeciAventurasDatabase? = null

        /**
         * Base de datos 100% local (Sección 23 del spec maestro: sin
         * Firebase, sin backend, sin red). [scope] se usa únicamente para
         * ejecutar la precarga de datos semilla la primera vez que se crea
         * el archivo físico de la base de datos.
         */
        fun getInstance(context: Context, scope: CoroutineScope): DeciAventurasDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, scope).also { instance = it }
            }
        }

        /**
         * Construye la base de datos con el `Callback.onCreate` de precarga
         * adjunto (ver [seedCallback]).
         *
         * `onCreate` solo se dispara la primera vez que SQLite crea el
         * archivo físico, y Room abre la base de datos de forma perezosa
         * (en la primera consulta real), nunca dentro de `.build()`. Por
         * eso [databaseProvider] es un closure evaluado en el momento en que
         * `onCreate` realmente se dispara, cuando `database` ya está asignada.
         */
        private fun buildDatabase(context: Context, scope: CoroutineScope): DeciAventurasDatabase {
            lateinit var database: DeciAventurasDatabase
            database = Room.databaseBuilder(
                context.applicationContext,
                DeciAventurasDatabase::class.java,
                DATABASE_NAME,
            )
                // Migración destructiva (versión 1→2 agregó user_preferences;
                // 2→3 agregó reflection en user_progress; 3→4 agregó
                // soundEnabled en user_preferences). Se acepta porque el
                // proyecto todavía no está publicado: no hay progreso real
                // de usuarios que proteger. ANTES de una release pública,
                // esto debe reemplazarse por Migration explícitas que
                // preserven los datos ya guardados.
                .fallbackToDestructiveMigration()
                .addCallback(seedCallback(databaseProvider = { database }, scope = scope))
                .build()
            return database
        }

        /**
         * Precarga los 5 dilemas y sus tarjetas de decisión (Sección 24 del
         * spec maestro: "la app instalada debe sentirse completa").
         *
         * Expuesta como `internal` (no `private`) para que los tests
         * instrumentados puedan adjuntar exactamente esta misma lógica a una
         * base de datos en memoria y verificar que la precarga real funciona,
         * en vez de duplicar la lógica de seed en el test.
         */
        internal fun seedCallback(
            databaseProvider: () -> DeciAventurasDatabase,
            scope: CoroutineScope,
        ): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    val database = databaseProvider()
                    database.dilemmaDao().insertAll(SeedData.dilemmas)
                    database.choiceDao().insertAll(SeedData.choices)
                }
            }
        }
    }
}
