package com.example.musicplayandroidai.ui.navigation

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayandroidai.ui.theme.*

@Immutable
data class GlassDockColors(
    val surfaceColor: Color,
    val borderColor: Color,
    val iconActiveColor: Color,
    val iconInactiveColor: Color,
    val pillActiveColor: Color
)

@Composable
fun getGlassDockColors(isDark: Boolean = isSystemInDarkTheme()): GlassDockColors {
    return if (isDark) {
        GlassDockColors(
            surfaceColor = Color.Black.copy(alpha = 0.4f), // Более прозрачный
            borderColor = Color.White.copy(alpha = 0.1f),
            iconActiveColor = Color(0xFF00E5FF), // Бирюзовый акцент как на макете
            iconInactiveColor = Color.White.copy(alpha = 0.6f),
            pillActiveColor = Color.White.copy(alpha = 0.05f)
        )
    } else {
        GlassDockColors(
            surfaceColor = Color.White.copy(alpha = 0.4f),
            borderColor = Color.Black.copy(alpha = 0.1f),
            iconActiveColor = Color(0xFF00B8D4),
            iconInactiveColor = Color.Black.copy(alpha = 0.6f),
            pillActiveColor = Color.Black.copy(alpha = 0.05f)
        )
    }
}

@Composable
fun GlassDock(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    GlassDockContent(
        currentRoute = currentRoute,
        onNavigate = { destination ->
            navController.navigate(destination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        modifier = modifier
    )
}

@Composable
fun GlassDockContent(
    currentRoute: String?,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    colors: GlassDockColors = getGlassDockColors()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val dockShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .height(80.dp)
            .clip(dockShape)
            .background(colors.surfaceColor)
            .border(1.dp, colors.borderColor, dockShape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomBarDestinations.forEach { destination ->
                DockTab(
                    label = destination.label,
                    icon = destination.icon,
                    isSelected = destination.route == currentRoute,
                    activeColor = colors.iconActiveColor,
                    inactiveColor = colors.iconInactiveColor,
                    onClick = { onNavigate(destination) }
                )
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
    val contentColor = if (isSelected) activeColor else inactiveColor
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(26.dp),
            tint = contentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
