package jun.money.mate.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jun.money.mate.designsystem.theme.LightBlue1
import jun.money.mate.designsystem.theme.LightBlue2

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    lineColor: Color = LightBlue2,
) {
    HorizontalDivider(
        modifier = modifier,
        color = lineColor,
        thickness = 1.dp
    )
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    lineColor: Color = LightBlue2
) {
    VerticalDivider(
        modifier = modifier,
        color = lineColor,
        thickness = 1.dp
    )
}

@Preview(showBackground = true)
@Composable
private fun HmHorizontalDividerPreview() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        lineColor = LightBlue1
    )
}