package com.deciaventuras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.deciaventuras.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE dilemmaId = :dilemmaId ORDER BY timestampMillis DESC")
    fun observeForDilemma(dilemmaId: Int): Flow<List<UserProgressEntity>>

    @Query("SELECT COUNT(*) FROM user_progress")
    suspend fun count(): Int

    @Insert
    suspend fun insert(progress: UserProgressEntity): Long

    /** Usado únicamente por el reinicio de progreso desde Ajustes (Sección "Reiniciar todo el progreso"). */
    @Query("DELETE FROM user_progress")
    suspend fun deleteAll()
}
