package jun.money.mate.data.resository

import jun.money.mate.data.mapper.toSaveEntity
import jun.money.mate.data.mapper.toSavePlan
import jun.money.mate.data_api.database.SaveRepository
import jun.money.mate.database.dao.SaveDao
import jun.money.mate.model.save.SavePlan
import jun.money.mate.model.save.SavePlanList
import kic.owner2.utils.etc.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class SaveRepositoryImpl @Inject constructor(
    private val saveDao: SaveDao
) : SaveRepository {

    override suspend fun upsertSavePlan(savePlan: SavePlan) {
        try {
            saveDao.upsert(savePlan.toSaveEntity())
        } catch (e: Exception) {
            Logger.e("upsertSavePlan error: $e")
        }
    }

    override fun getSavePlanListFlow(): Flow<SavePlanList> {
        return saveDao.getFlow().map { list ->
            // 현재 월이 아닌 경우 실행 여부를 false로 변경하고 실행 횟수를 증가시킴
            val savePlans = list.map { it.toSavePlan() }
            savePlans.map { savePlan ->
                if (savePlan.executeMonth != LocalDate.now().monthValue) {
                    savePlan.copy(
                        executed = false,
                        executeCount = if (savePlan.executed) savePlan.executeCount + 1 else savePlan.executeCount
                    )
                } else {
                    savePlan
                }
            }
            SavePlanList(savePlans = list.map { it.toSavePlan() })
        }.catch {
            Logger.e("getSavePlanListFlow error: $it")
        }
    }

    override suspend fun getSavePlan(id: Long): SavePlan {
        return saveDao.get(id).toSavePlan()
    }

    override suspend fun updateExecuteState(id: Long, isExecuted: Boolean) {
        try {
            saveDao.updateExecuteState(id, LocalDate.now().monthValue, isExecuted)
        } catch (e: Exception) {
            Logger.e("updateExecuteState error: $e")
        }
    }

    override suspend fun deleteById(id: Long) {
        try {
            saveDao.deleteById(id)
        } catch (e: Exception) {
            Logger.e("deleteById error: $e")
        }
    }
}