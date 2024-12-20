package jun.money.mate.spending_plan

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jun.money.mate.data_api.database.SpendingPlanRepository
import jun.money.mate.domain.GetSpendingPlanUsecase
import jun.money.mate.model.Utils
import jun.money.mate.model.etc.ViewMode
import jun.money.mate.model.etc.error.MessageType
import jun.money.mate.model.spending.ConsumptionSpend
import jun.money.mate.model.spending.SpendingPlan
import jun.money.mate.model.spending.SpendingPlanList
import jun.money.mate.model.spending.SpendingType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SpendingPlanListViewModel @Inject constructor(
    private val spendingPlanRepository: SpendingPlanRepository,
    private val getSpendingPlanUsecase: GetSpendingPlanUsecase
) : ViewModel() {

    private val _spendingPlanListState =
        MutableStateFlow<SpendingPlanListState>(SpendingPlanListState.Loading)
    val spendingPlanListState: StateFlow<SpendingPlanListState> = _spendingPlanListState.onStart {
        loadSpending()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpendingPlanListState.Loading
    )

    val spendingListViewMode: StateFlow<ViewMode> = spendingPlanListState.flatMapLatest {
        flowOf(
            when (it) {
                is SpendingPlanListState.SpendingPlanListData -> it.spendingListViewMode
                else -> ViewMode.LIST
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ViewMode.LIST
    )

    private val _spendingPlanListEffect = MutableSharedFlow<SpendingPlanListEffect>()
    val spendingPlanListEffect: SharedFlow<SpendingPlanListEffect> get() = _spendingPlanListEffect.asSharedFlow()

    private fun loadSpending() {
        viewModelScope.launch {
            getSpendingPlanUsecase().collect { spendingPlanData ->
                _spendingPlanListState.value =
                    if (spendingPlanData.spendingPlanList.spendingPlans.isEmpty()) {
                        SpendingPlanListState.Empty
                    } else {
                        SpendingPlanListState.SpendingPlanListData(
                            spendingPlanData.spendingPlanList,
                            spendingPlanData.consumptionPlan
                        )
                    }
            }
        }
    }

    fun changeSpendingSelected(spendingPlan: SpendingPlan) {
        val spendingListState =
            spendingPlanListState.value as? SpendingPlanListState.SpendingPlanListData ?: return

        viewModelScope.launch {
            _spendingPlanListState.update {
                spendingListState.copy(
                    spendingPlanList = spendingListState.spendingPlanList.copy(
                        spendingPlans = spendingListState.spendingPlanList.spendingPlans.map {
                            if (it.id == spendingPlan.id) {
                                it.copy(selected = !it.selected)
                            } else {
                                it.copy(selected = false)
                            }
                        },
                    ),
                    consumptionPlan = spendingListState.consumptionPlan.map { consumptionSpend ->
                        if (consumptionSpend.spendingPlan.id == spendingPlan.id) {
                            consumptionSpend.copy(
                                spendingPlan = consumptionSpend.spendingPlan.copy(
                                    selected = !consumptionSpend.spendingPlan.selected
                                )
                            )
                        } else {
                            consumptionSpend.copy(
                                spendingPlan = consumptionSpend.spendingPlan.copy(selected = false)
                            )
                        }
                    }
                )
            }
        }
    }

    fun editSpending() {
        val spendingListState = spendingPlanListState.value as? SpendingPlanListState.SpendingPlanListData ?: return
        val selectedId = spendingListState.selectedId ?: return

        viewModelScope.launch {
            _spendingPlanListEffect.emit(SpendingPlanListEffect.EditSpendingPlan(selectedId))
        }
    }

    fun deleteSpending() {
        val spendingListState = spendingPlanListState.value as? SpendingPlanListState.SpendingPlanListData ?: return
        val selectedId = spendingListState.selectedId ?: return

        viewModelScope.launch {
            spendingPlanRepository.deleteById(selectedId)
            showSnackBar(MessageType.Message("지출 계획이 삭제되었습니다"))
        }
    }

    fun spendingTabClick(index: Int) {
        val spendingListState = spendingPlanListState.value as? SpendingPlanListState.SpendingPlanListData ?: return

        viewModelScope.launch {
            _spendingPlanListState.update {
                spendingListState.copy(spendingTypeTabIndex = index)
            }
        }
    }

    private fun showSnackBar(messageType: MessageType) {
        viewModelScope.launch {
            _spendingPlanListEffect.emit(SpendingPlanListEffect.ShowSnackBar(messageType))
        }
    }
}

@Stable
internal sealed interface SpendingPlanListState {

    @Immutable
    data object Loading : SpendingPlanListState

    @Immutable
    data object Empty : SpendingPlanListState

    @Immutable
    data class SpendingPlanListData(
        val spendingPlanList: SpendingPlanList,
        val consumptionPlan: List<ConsumptionSpend>,
        val spendingTypeTabIndex: Int = 0,
    ) : SpendingPlanListState {

        val spendingListViewMode: ViewMode get() = if (spendingPlanList.spendingPlans.any { it.selected }) ViewMode.EDIT else ViewMode.LIST

        private val selectedSpendingType: SpendingType get() = SpendingType.entries[spendingTypeTabIndex]

        private val consumptionTotal get() = consumptionPlan.sumOf { it.consumptionTotal }
        private val realTotal get() = spendingPlanList.predictTotal + consumptionTotal
        val totalString get() = spendingPlanList.totalString
        val realTotalString get() = Utils.formatAmountWon(realTotal)

        val filterSpendingPlanList: SpendingPlanList
            get() = spendingPlanList.copy(
                spendingPlans = if (selectedSpendingType == SpendingType.ALL) {
                    spendingPlanList.spendingPlans
                } else {
                    spendingPlanList.spendingPlans.filter { it.type == selectedSpendingType }
                }
            )

        val filterConsumptionPlan: List<ConsumptionSpend>
            get() = consumptionPlan.filter { selectedSpendingType == SpendingType.ConsumptionPlan || selectedSpendingType == SpendingType.ALL }

        val selectedId get() = spendingPlanList.spendingPlans.firstOrNull { it.selected }?.id
    }
}

@Stable
internal sealed interface SpendingPlanListEffect {

    @Immutable
    data class ShowSnackBar(val messageType: MessageType) : SpendingPlanListEffect

    @Immutable
    data class EditSpendingPlan(val id: Long) : SpendingPlanListEffect
}