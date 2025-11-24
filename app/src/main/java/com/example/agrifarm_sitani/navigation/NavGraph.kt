package com.example.agrifarm_sitani.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.agrifarm_sitani.ui.screen.HomeScreen
import com.example.agrifarm_sitani.ui.screen.PlantingCalendarScreen
import com.example.agrifarm_sitani.ui.screen.SeedCalculatorScreen
import com.example.agrifarm_sitani.ui.screen.CalculationHistoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SeedCalculator : Screen("seed_calculator")
    object SeedCalculatorHistory : Screen("seed_calculator_history")
    object PlantingCalendar : Screen("planting_calendar")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSeedCalculator = {
                    navController.navigate(Screen.SeedCalculator.route)
                },
                onNavigateToPlantingCalendar = {
                    navController.navigate(Screen.PlantingCalendar.route)
                }
            )
        }

        composable(Screen.SeedCalculator.route) {
            SeedCalculatorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.SeedCalculatorHistory.route)
                }
            )
        }

        composable(Screen.SeedCalculatorHistory.route) {
            CalculationHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PlantingCalendar.route) {
            PlantingCalendarScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
