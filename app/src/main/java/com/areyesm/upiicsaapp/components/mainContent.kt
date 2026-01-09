package com.areyesm.upiicsaapp.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.areyesm.upiicsaapp.R
import com.areyesm.upiicsaapp.model.HomeModel
import com.areyesm.upiicsaapp.ui.theme.ColorShadowPrimary
import com.areyesm.upiicsaapp.ui.theme.ColorSurface
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    section: HomeModel
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = ColorSurface
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 18.dp,
                horizontal = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            when (section) {
                HomeModel.MAP -> mainContentHeader(
                    icon = R.drawable.gps,
                    title = "Mapa"
                )
                HomeModel.HOME -> mainContentHeader(
                    icon = R.drawable.newspaper,
                    title = "Principal"
                )
                HomeModel.BACKPACK -> mainContentHeader(
                    icon = R.drawable.book,
                    title = "Mi Mochila"
                )
            }

            // Contenido
            AnimatedContent(
                targetState = section,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith
                            fadeOut(tween(180))
                },
                label = "MainContentBody",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { targetSection ->
                when (targetSection) {
                    HomeModel.MAP -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ){
                            Map(modifier.fillMaxSize())
                        }
                    }
                    HomeModel.HOME -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ){
                            News()
                        }
                    }
                    HomeModel.BACKPACK -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ){
                            Backpack()
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun mainContentHeader(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "MainContentHeaderItem",
                modifier = Modifier.size(28.dp),
                tint = null
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp
                )
            )
        }
    }
}
