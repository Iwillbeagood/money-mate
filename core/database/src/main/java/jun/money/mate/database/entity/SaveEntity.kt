package jun.money.mate.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jun.money.mate.database.AppDatabase
import jun.money.mate.model.save.SaveCategory

@Entity(tableName = AppDatabase.SAVING_PLAN_TABLE_NAME)
data class SaveEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val amount: Long,
    val planDay: Int,
    val saveCategory: SaveCategory,
    val executeMonth: Int,
    val executed: Boolean,
)