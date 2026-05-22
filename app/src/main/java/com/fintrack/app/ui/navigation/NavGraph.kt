package com.fintrack.app.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fintrack.app.ui.screens.budgets.BudgetsScreen
import com.fintrack.app.ui.screens.dashboard.DashboardScreen
import com.fintrack.app.ui.screens.goals.GoalsScreen
import com.fintrack.app.ui.screens.settings.SettingsScreen
import com.fintrack.app.ui.screens.transactions.TransactionsScreen
import com.fintrack.app.ui.theme.AccentLime
import com.fintrack.app.ui.theme.TextDisabled

@Composable
fun FinTrackNavGraph() {
    val navController = rememberNavController()
    
    val items = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Budgets,
        Screen.Goals,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    val scale = remember(isSelected) { Animatable(1f) }
                    LaunchedEffect(isSelected) {
                        if (isSelected) {
                            scale.animateTo(
                                targetValue = 1.0f,
                                animationSpec = keyframes {
                                    durationMillis = 200
                                    1.0f at 0
                                    1.25f at 100
                                    1.0f at 200
                                }
                            )
                        } else {
                            scale.snapTo(1f)
                        }
                    }

                    NavigationBarItem(
                        icon = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.scale(scale.value)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.size(4.dp)) {
                                    val dotAlpha by androidx.compose.animation.core.animateFloatAsState(
                                        targetValue = if (isSelected) 1f else 0f,
                                        animationSpec = tween(200)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(color = AccentLime.copy(alpha = dotAlpha), shape = CircleShape)
                                    )
                                }
                            }
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentLime,
                            unselectedIconColor = TextDisabled,
                            indicatorColor = Color.Black
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(280, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it/3 }, animationSpec = tween(280)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it/3 }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Transactions.route) { TransactionsScreen() }
            composable(Screen.Budgets.route) { BudgetsScreen() }
            composable(Screen.Goals.route) { GoalsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
