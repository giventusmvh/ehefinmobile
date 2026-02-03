package com.example.ehefin_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ehefin_mobile.feature.auth.presentation.screen.LoginScreen
import com.example.ehefin_mobile.feature.auth.presentation.screen.RegisterScreen
import com.example.ehefin_mobile.feature.home.presentation.screen.HomeScreen
import com.example.ehefin_mobile.feature.loan.presentation.screen.LoanDetailScreen
import com.example.ehefin_mobile.feature.loan.presentation.screen.LoanListScreen
import com.example.ehefin_mobile.feature.loan.presentation.screen.SubmitLoanScreen
import com.example.ehefin_mobile.feature.plafond.presentation.PlafondScreen
import com.example.ehefin_mobile.feature.profile.presentation.EditProfileScreen
import com.example.ehefin_mobile.feature.profile.presentation.ProfileScreen

import androidx.compose.runtime.LaunchedEffect

/** Main navigation graph for the app */
@Composable
fun EheFinNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Flow
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        composable(Screen.ForgotPassword.route) {
            com.example.ehefin_mobile.feature.auth.presentation.screen.ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onNavigateToPlafond = {
                    if (isLoggedIn) {
                        navController.navigate(Screen.Plafond.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onNavigateToSubmitLoan = {
                    if (isLoggedIn) {
                        navController.navigate(Screen.SubmitLoan.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onLogout = {
                    // For guests this acts as Login navigation
                    navController.navigate(Screen.Login.route) {
                         if (!isLoggedIn) {
                             // If guest, we just go to login, keep backstack? 
                             // Usually better to behave like regular nav
                         } else {
                             // If logout, clear backstack
                             popUpTo(Screen.Home.route) { inclusive = true }
                         }
                    }
                }
            )
        }

        // Profile Flow
        composable(Screen.Profile.route) { 
             if (isLoggedIn) {
                 ProfileScreen(navController = navController)
             } else {
                 // Redirect guests to Login
                 LaunchedEffect(Unit) {
                     navController.navigate(Screen.Login.route)
                 }
             }
        }

        composable(Screen.EditProfile.route) { EditProfileScreen(navController = navController) }

        // Plafond Flow
        composable(Screen.Plafond.route) { PlafondScreen(navController = navController) }

        // Loan Flow
        composable(Screen.LoanList.route) {
            LoanListScreen(
                onNavigateToDetail = { loanId ->
                    navController.navigate(Screen.LoanDetail.createRoute(loanId))
                },
                onNavigateToSubmit = { navController.navigate(Screen.SubmitLoan.route) }
            )
        }

        composable(
            route = Screen.LoanDetail.route,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong("loanId") ?: 0L
            LoanDetailScreen(
                loanId = loanId,
                onNavigateToHistory = {
                    navController.navigate(Screen.LoanHistory.createRoute(loanId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SubmitLoan.route) {
            SubmitLoanScreen(
                onLoanSubmitted = {
                    navController.navigate(Screen.LoanList.route) {
                        popUpTo(Screen.SubmitLoan.route) { inclusive = true }
                    }
                },
                onNavigateToPlafond = {
                     navController.navigate(Screen.Plafond.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}