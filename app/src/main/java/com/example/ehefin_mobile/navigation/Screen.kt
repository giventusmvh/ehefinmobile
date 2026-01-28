package com.example.ehefin_mobile.navigation

/**
 * Sealed class defining all navigation routes
 */
sealed class Screen(val route: String) {
    
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Main
    object Home : Screen("home")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    
    // Plafond
    object Plafond : Screen("plafond")
    object SelectPlafond : Screen("select_plafond")
    
    // Loans
    object LoanList : Screen("loan_list")
    object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: Long) = "loan_detail/$loanId"
    }
    object SubmitLoan : Screen("submit_loan")
    object LoanHistory : Screen("loan_history/{loanId}") {
        fun createRoute(loanId: Long) = "loan_history/$loanId"
    }
    
    // Settings
    object Settings : Screen("settings")
}