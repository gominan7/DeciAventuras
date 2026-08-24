package com.deciaventuras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deciaventuras.app.data.local.entity.ChoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoiceDao {

    @Query("SELECT * FROM choices WHERE dilemmaId = :dilemmaId ORDER BY orderIndex ASC")
    fun observeForDilemma(dilemmaId: Int): Flow<List<ChoiceEntity>>

    @Query("SELECT * FROM choices WHERE id = :choiceId LIMIT 1")
    suspend fun getById(choiceId: Int): ChoiceEntity?

    @Query("SELECT COUNT(*) FROM choices")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(choices: List<ChoiceEntity>)
}
