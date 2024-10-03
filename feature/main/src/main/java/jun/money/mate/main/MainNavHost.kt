package jun.money.mate.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import jun.money.mate.home.navigation.homeNavGraph
import jun.money.mate.main.component.MainNavigator
import jun.money.mate.model.etc.error.MessageType

@Composable
internal fun MainNavHost(
    paddingValues: PaddingValues,
    navigator: MainNavigator,
    appRestart: () -> Unit,
    onShowErrorSnackBar: (MessageType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(paddingValues)
    ) {
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            homeNavGraph(
                onShowErrorSnackBar = onShowErrorSnackBar,
            )
        }
    }
}