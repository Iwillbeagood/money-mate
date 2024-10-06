package jun.money.mate.data_api.database

import jun.money.mate.model.income.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    suspend fun upsertIncome(income: Income)

    fun getIncomeFlow(): Flow<List<Income>>

    suspend fun deleteById(id: Long)
}