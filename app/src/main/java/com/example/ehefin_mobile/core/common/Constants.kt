package com.example.ehefin_mobile.core.common

object Constants {
    // Shared Preferences / DataStore Keys
    const val AUTH_PREFERENCES = "auth_preferences"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Room Database
    const val DATABASE_NAME = "ehefin_database"
    const val DATABASE_VERSION = 1
    
    // API Endpoints
    object Endpoints {
        // Auth
        const val AUTH_LOGIN = "auth/login"
        const val AUTH_REGISTER = "auth/register"
        const val AUTH_LOGOUT = "auth/logout"
        const val AUTH_FORGOT_PASSWORD = "auth/forgot-password"
        const val AUTH_RESET_PASSWORD = "auth/reset-password"
        const val AUTH_GOOGLE_LOGIN = "auth/google-login"
        
        // Customer
        const val CUSTOMER_PROFILE = "customer/profile"
        const val CUSTOMER_PLAFOND = "customer/plafond"
        
        // Products & Branches (Public)
        const val PRODUCTS = "products"
        const val BRANCHES = "branches"
        
        // Loans
        const val LOANS = "loans"
        const val LOAN_DETAIL = "loans/{id}"
        const val LOAN_HISTORY = "loans/{id}/history"
    }
    
    // Loan Statuses
    object LoanStatus {
        const val SUBMITTED = "SUBMITTED"
        const val MARKETING_APPROVED = "MARKETING_APPROVED"
        const val MARKETING_REJECTED = "MARKETING_REJECTED"
        const val BRANCH_MANAGER_APPROVED = "BRANCH_MANAGER_APPROVED"
        const val BRANCH_MANAGER_REJECTED = "BRANCH_MANAGER_REJECTED"
        const val APPROVED = "APPROVED"
        const val REJECTED = "REJECTED"
    }
    
    // Product Types
    object ProductType {
        const val BRONZE = "BRONZE"
        const val SILVER = "SILVER"
        const val GOLD = "GOLD"
        const val PLATINUM = "PLATINUM"
    }
}