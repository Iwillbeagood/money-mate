package jun.money.mate.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import jun.money.mate.designsystem.theme.Black
import jun.money.mate.designsystem.theme.JUNTheme
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun MainBottomBar(
    visible: Boolean,
    bottomItems: PersistentList<MainBottomNavItem>,
    currentItem: MainBottomNavItem?,
    onBottomItemClicked: (MainBottomNavItem) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) }
    ) {
        NavigationBar {
            bottomItems.forEach { item ->
                val title = stringResource(item.titleRes)

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = title,
                            modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = Black,
                        unselectedTextColor = Gray
                    ),
                    label = {
                        Text(
                            text = title,
                            style = JUNTheme.typography.labelLargeR
                        )
                    },
                    onClick = { onBottomItemClicked(item) },
                    selected = item == currentItem
                )
            }
        }
    }
}