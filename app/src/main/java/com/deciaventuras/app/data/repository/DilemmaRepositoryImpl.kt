package com.deciaventuras.app.data.repository

import com.deciaventuras.app.data.local.dao.ChoiceDao
import com.deciaventuras.app.data.local.dao.DilemmaDao
import com.deciaventuras.app.data.local.dao.UserProgressDao
import com.deciaventuras.app.data.local.database.SeedData
import com.deciaventuras.app.data.local.entity.toDomain
import com.deciaventuras.app.data.local.entity.toEntity
import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.UserProgress
import com.deciaventuras.app.domain.repository.DilemmaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DilemmaRepositoryImpl(
    private val dilemmaDao: DilemmaDao,
    private val choiceDao: ChoiceDao,
    private val userProgressDao: UserProgressDao,
) : DilemmaRepository {

    override fun observeDilemmas(): Flow<List<Dilemma>> =
        dilemmaDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getDilemma(dilemmaId: Int): Dilemma? =
        dilemmaDao.getById(dilemmaId)?.toDomain()

    override fun observeChoices(dilemmaId: Int): Flow<List<Choice>> =
        choiceDao.observeForDilemma(dilemmaId).map { list -> list.map { it.toDomain() } }

    override suspend fun getChoice(choiceId: Int): Choice? =
        choiceDao.getById(choiceId)?.toDomain()

    override fun observeProgress(): Flow<List<UserProgress>> =
        userProgressDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertProgress(progress: UserProgress) {
        userProgressDao.insert(progress.toEntity())
    }

    override suspend fun setDilemmaCompleted(dilemmaId: Int, isCompleted: Boolean) {
        dilemmaDao.setCompleted(dilemmaId, isCompleted)
    }

    override suspend fun setDilemmaUnlocked(dilemmaId: Int, isUnlocked: Boolean) {
        dilemmaDao.setUnlocked(dilemmaId, isUnlocked)
    }

    override suspend fun resetAllProgress() {
        userProgressDao.deleteAll()
        // OnConflictStrategy.REPLACE sobrescribe cada fila con los valores
        // semilla originales (orderIndex 0 desbloqueado, el resto bloqueado,
        // ninguno completado), sin necesitar una query de "reset" aparte.
        dilemmaDao.insertAll(SeedData.dilemmas)
    }
}
