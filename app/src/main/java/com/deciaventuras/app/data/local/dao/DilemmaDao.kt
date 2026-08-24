package com.deciaventuras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deciaventuras.app.data.local.entity.DilemmaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DilemmaDao {

    @Query("SELECT * FROM dilemmas ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<DilemmaEntity>>

    @Query("SELECT * FROM dilemmas WHERE id = :dilemmaId LIMIT 1")
    suspend fun getById(dilemmaId: Int): DilemmaEntity?

    @Query("SELECT COUNT(*) FROM dilemmas")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dilemmas: List<DilemmaEntity>)

    @Query("UPDATE dilemmas SET isCompleted = :isCompleted WHERE id = :dilemmaId")
    suspend fun setCompleted(dilemmaId: Int, isCompleted: Boolean)

    @Query("UPDATE dilemmas SET isUnlocked = :isUnlocked WHERE id = :dilemmaId")
    suspend fun setUnlocked(dilemmaId: Int, isUnlocked: Boolean)
}
