package com.example.musicplayandroidai.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun GlassDock(
    navController: NavHostController,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val dockShape = RoundedCornerShape(24.dp)
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInHorizontally(
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 220),
                initialOffsetX = { it }
            ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 220)),
            exit = slideOutHorizontally(
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 220),
                targetOffsetX = { it }
            ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(durationMillis = 220))
        ) {
            Surface(
                shape = dockShape,
                color = surfaceColor,
                border = BorderStroke(1.dp, borderColor),
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DockHandleButton(symbol = ">", onClick = onToggleExpanded)
                    bottomBarDestinations.forEach { destination ->
                        val isSelected = destination.route == currentRoute
                        DockTab(
                            label = destination.label,
                            isSelected = isSelected,
                            activeColor = activeColor,
                            inactiveColor = inactiveColor,
                            onClick = {
                                if (currentRoute != destination.route) {
                                    navController.navigate(destination.route) {
                                        launchSingleTop = true
                                        popUpTo(AppDestination.Library.route) { saveState = true }
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = !isExpanded,
            enter = slideInHorizontally(
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 220),
                initialOffsetX = { -it / 2 }
            ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 220)),
            exit = slideOutHorizontally(
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 220),
                targetOffsetX = { -it / 2 }
            ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(durationMillis = 220))
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = surfaceColor,
                border = BorderStroke(1.dp, borderColor),
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                DockHandleButton(symbol = "<", onClick = onToggleExpanded)
            }
        }
    }
}

@Composable
private fun DockTab(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val highlightColor = if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent
    val textColor = if (isSelected) activeColor else inactiveColor
    Box(
        modifier = Modifier
            .background(color = highlightColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun DockHandleButton(
    symbol: String,
    onClick: () -> Unit,
    size: Dp = 48.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
