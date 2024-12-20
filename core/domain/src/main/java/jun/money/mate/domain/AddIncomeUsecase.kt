package jun.money.mate.domain

import jun.money.mate.data_api.database.IncomeRepository
import jun.money.mate.model.etc.error.MessageType
import jun.money.mate.model.income.Income
import jun.money.mate.model.income.IncomeType
import java.time.LocalDate
import javax.inject.Inject

class AddIncomeUsecase @Inject constructor(
    private val incomeRepository: IncomeRepository
) {

    suspend operator fun invoke(
        id: Long,
        title: String,
        amount: Long,
        type: IncomeType?,
        incomeDate: LocalDate,
        onSuccess: () -> Unit,
        onError: (MessageType) -> Unit
    ) {
        if (title.isEmpty()) {
            onError(MessageType.Message("수입명을 입력해 주세요"))
            return
        }

        if (amount <= 0) {
            onError(MessageType.Message("수입액을 입력해 주세요"))
            return
        }

        if (type == null) {
            onError(MessageType.Message("수입 타입을 선택해 주세요"))
            return
        }

        incomeRepository.upsertIncome(
            Income(
                id = id,
                title = title,
                amount = amount,
                type = type,
                incomeDate = incomeDate,
            )
        )
        onSuccess()
    }
}