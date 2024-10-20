package jun.money.mate.income.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jun.money.mate.income.IncomeAddRoute
import jun.money.mate.income.IncomeListRoute
import jun.money.mate.model.etc.error.MessageType
import jun.money.mate.navigation.Route

fun NavController.navigateToIncomeList() {
    navigate(Route.Income.List)
}

fun NavController.navigateToIncomeAdd() {
    navigate(Route.Income.Add)
}

fun NavGraphBuilder.incomeNavGraph(
    onGoBack: () -> Unit,
    onShowIncomeAdd: () -> Unit,
    onShowSnackBar: (MessageType) -> Unit
) {

    composable<Route.Income.List> {
        IncomeListRoute(
            onGoBack = onGoBack,
            onShowIncomeAdd = onShowIncomeAdd
        )
    }

    composable<Route.Income.Add> {
        IncomeAddRoute(
            onGoBack = onGoBack,
            onShowSnackBar = onShowSnackBar
        )
    }
}
