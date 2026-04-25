package com.mdzeldemt.versati.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.max

@Composable
fun WrapperLayout(
    pivotSize: Dp,
    pivotContent: @Composable () -> Unit,
    wrapperContent: @Composable () -> Unit
) {
    Layout(
        content = {
            pivotContent()
            wrapperContent()
        },
        modifier = Modifier.fillMaxSize()
    ) { measurables, constraints ->
        val pivotMeasurable = measurables.first()
        val wrapperMeasurables = measurables.drop(1)

        val pivotPlaceable = pivotMeasurable.measure(
            Constraints(
                maxWidth = pivotSize.roundToPx(),
                maxHeight = pivotSize.roundToPx()
            )
        )

        val textPlaceables = mutableListOf<Placeable>()
        val totalHeight = wrapperMeasurables.fold(0) { y, measurable ->
            val placeable = measurable.measure(
                Constraints(
                    maxWidth =
                    if (y > pivotPlaceable.height)
                        constraints.maxWidth
                    else
                        constraints.maxWidth - pivotPlaceable.width
                )
            )
            textPlaceables.add(placeable)
            y + placeable.height
        }

        layout(constraints.maxWidth, max(totalHeight, pivotPlaceable.height)) {
            pivotPlaceable.place(
                x = constraints.maxWidth - pivotPlaceable.width,
                y = 0
            )

            textPlaceables.fold(0) { y, placeable ->
                placeable.place(x = 0, y = y)
                y + placeable.height
            }
        }
    }
}