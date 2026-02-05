package com.example.musicplayandroidai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayandroidai.ui.theme.GlassBlack
import com.example.musicplayandroidai.ui.theme.GlassBorderDark
import com.example.musicplayandroidai.ui.theme.GlassBorderLight
import com.example.musicplayandroidai.ui.theme.GlassWhite

@Composable
fun GlassDock(
    navController: NavHostController,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isDark = isSystemInDarkTheme()

    val surfaceColor = if (isDark) GlassBlack else GlassWhite
    val borderColor = if (isDark) GlassBorderDark else GlassBorderLight
    val dockShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Развернутая панель
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(260)),
            exit = fadeOut(tween(200)) + shrinkHorizontally(tween(260))
        ) {
            Box(
                modifier = Modifier
                    .clip(dockShape)
                    .background(surfaceColor)
                    .border(1.dp, borderColor, dockShape)
                    .wrapContentWidth()
            ) {
                Row(
                    modifier = Modifier
                        .height(72.dp) // Увеличили высоту для иконок + текста
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DockHandleButton(symbol = ">", onClick = onToggleExpanded, size = 44.dp)
                    
                    bottomBarDestinations.forEach { destination ->
                        val isSelected = destination.route == currentRoute
                        DockTab(
                            label = destination.label,
                            icon = destination.icon,
                            isSelected = isSelected,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Свернутая ручка
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -it },
            exit = fadeOut(tween(200)) + slideOutHorizontally(tween(220)) { -it },
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            val handleShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .clip(handleShape)
                    .background(surfaceColor)
                    .border(1.dp, borderColor, handleShape)
            ) {
                DockHandleButton(symbol = "<", onClick = onToggleExpanded, size = 52.dp)
            }
        }
    }
}

@Composable
private fun DockTab(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val pillColor = activeColor.copy(alpha = 0.15f)
    val contentColor = if (isSelected) activeColor else inactiveColor
    
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) pillColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DockHandleButton(
    symbol: String,
    onClick: () -> Unit,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2))
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
