@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.areyesm.upiicsaapp.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.HomeModel
import com.areyesm.upiicsaapp.ui.theme.ColorGradient1
import com.areyesm.upiicsaapp.ui.theme.ColorGradient2
import com.areyesm.upiicsaapp.ui.theme.ColorGradient3
import com.areyesm.upiicsaapp.ui.theme.ColorSurface


@Composable
fun FooterBar(
    modifier: Modifier = Modifier,
    currentSection: HomeModel,
    onSectionSelected: (HomeModel) -> Unit
) {
    val role = getLoginProviderLabel()
    if (role == "Bienvenido") {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(32.dp),
            color = ColorSurface
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                //Control de animación
                val itemWidth = maxWidth / 3

                val indicatorOffset by animateDpAsState(
                    targetValue = when (currentSection) {
                        HomeModel.MAP -> 7.dp
                        HomeModel.HOME -> itemWidth
                        HomeModel.BACKPACK -> itemWidth * 1.93f
                    },
                    label = "FooterIndicator"
                )
                // Indicador ovalado
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .padding(top = 6.dp)
                        .width(itemWidth)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.linearGradient(
                                0f to ColorGradient1,
                                0.25f to ColorGradient2,
                                1f to ColorGradient3
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = 10.dp,
                            horizontal = 10.dp
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FooterItem(
                        icon = R.drawable.map,
                        isSelected = currentSection == HomeModel.MAP,
                        onClick = { onSectionSelected(HomeModel.MAP) }
                    )

                    FooterItem(
                        icon = R.drawable.home,
                        isSelected = currentSection == HomeModel.HOME,
                        onClick = { onSectionSelected(HomeModel.HOME) }
                    )

                    FooterItem(
                        icon = R.drawable.backpack,
                        isSelected = currentSection == HomeModel.BACKPACK,
                        onClick = { onSectionSelected(HomeModel.BACKPACK) }
                    )
                }
            }
        }
    } else {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(32.dp),
            color = ColorSurface
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                //Control de animación
                val itemWidth = maxWidth / 2

                val indicatorOffset by animateDpAsState(
                    targetValue = when (currentSection) {
                        HomeModel.MAP -> 7.dp
                        HomeModel.HOME -> itemWidth
                        HomeModel.BACKPACK -> itemWidth * 1.93f
                    },
                    label = "FooterIndicator"
                )
                // Indicador ovalado
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .padding(top = 6.dp)
                        .width(itemWidth)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.linearGradient(
                                0f to ColorGradient1,
                                0.25f to ColorGradient2,
                                1f to ColorGradient3
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = 10.dp,
                            horizontal = 10.dp
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FooterItem(
                        icon = R.drawable.map,
                        isSelected = currentSection == HomeModel.MAP,
                        onClick = { onSectionSelected(HomeModel.MAP) }
                    )

                    FooterItem(
                        icon = R.drawable.home,
                        isSelected = currentSection == HomeModel.HOME,
                        onClick = { onSectionSelected(HomeModel.HOME) }
                    )
                }
            }
        }
    }

}
