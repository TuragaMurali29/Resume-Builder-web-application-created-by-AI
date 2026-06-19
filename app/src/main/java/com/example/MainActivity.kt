package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.AppViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        val viewModel: AppViewModel = viewModel()
        val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

        NavHost(
            navController = navController,
            startDestination = "landing"
        ) {
            composable("landing") {
                LandingPageScreen(
                    viewModel = viewModel,
                    onNavToAuth = { navController.navigate("auth") },
                    onNavToChecker = { navController.navigate("checker") },
                    onNavToBuilder = {
                        if (isUserLoggedIn) navController.navigate("builder")
                        else navController.navigate("auth")
                    },
                    onNavToPricing = { navController.navigate("pricing") }
                )
            }
            
            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate("dashboard") {
                            popUpTo("landing") { inclusive = false }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onEditResume = { navController.navigate("builder") },
                    onEditCoverLetter = { navController.navigate("letter") },
                    onNavToChecker = { navController.navigate("checker") },
                    onNavToTracker = { navController.navigate("tracker") },
                    onNavToPrep = { navController.navigate("prep") },
                    onNavToJobSearch = { navController.navigate("jobsearch") },
                    onNavToPricing = { navController.navigate("pricing") },
                    onLogout = {
                        viewModel.logoutSimulated()
                        navController.navigate("landing") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("builder") {
                ResumeBuilderScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("checker") {
                ResumeCheckerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("letter") {
                CoverLetterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("tracker") {
                JobTrackerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("prep") {
                InterviewPrepScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("jobsearch") {
                AiJobSearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("pricing") {
                PricingScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
      }
    }
  }
}

