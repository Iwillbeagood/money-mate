package jun.money.mate.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jun.money.mate.database.AppDatabase
import jun.money.mate.database.util.generateInsertQuery
import jun.money.mate.model.spending.SpendingType
import java.time.LocalDate

@Entity(tableName = AppDatabase.SPENDING_PLAN_TABLE_NAME)
data class SpendingPlanEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val type: SpendingType,
    val spendingCategoryName: String,
    val amount: Long,
    val planDate: LocalDate,
    val isApply: Boolean = false
) {

    companion object {
        private val DEFAULT_SPENDING_PLAN = SpendingPlanEntity(
            id = System.currentTimeMillis(),
            title = "생활비",
            type = SpendingType.ConsumptionPlan,
            spendingCategoryName = "",
            amount = 0,
            planDate = LocalDate.now(),
            isApply = false
        )

        val INSERT_QUERY = generateInsertQuery(DEFAULT_SPENDING_PLAN, AppDatabase.SPENDING_PLAN_TABLE_NAME)
    }
}