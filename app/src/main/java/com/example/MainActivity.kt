package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.model.MainTab
import com.example.ui.screens.*
import com.example.ui.theme.IhwTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.IhwViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: IhwViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IhwTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: IhwViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val confirmedBooking by viewModel.confirmedBooking.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.CUSTOMER_HOME -> {
                        CustomerHomeScreen(
                            viewModel = viewModel,
                            onBookNow = { service -> viewModel.startBooking(preselectedService = service) },
                            onViewAllTransformations = { viewModel.navigateTo(AppScreen.TRANSFORMATIONS_GALLERY) },
                            onOpenMyBookings = { viewModel.setMainTab(MainTab.BOOKINGS) },
                            onAdminLogin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                        )
                    }

                    AppScreen.BOOKING_FLOW -> {
                        BookingFlowScreen(
                            viewModel = viewModel,
                            onClose = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) }
                        )
                    }

                    AppScreen.BOOKING_CONFIRMATION -> {
                        BookingConfirmationScreen(
                            booking = confirmedBooking,
                            viewModel = viewModel,
                            onHomeClick = {
                                viewModel.setMainTab(MainTab.HOME)
                                viewModel.navigateTo(AppScreen.CUSTOMER_HOME)
                            },
                            onMyBookingsClick = {
                                viewModel.setMainTab(MainTab.BOOKINGS)
                                viewModel.navigateTo(AppScreen.CUSTOMER_HOME)
                            }
                        )
                    }

                    AppScreen.TRANSFORMATIONS_GALLERY -> {
                        TransformationsGalleryScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) }
                        )
                    }

                    AppScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onLogout = { viewModel.logoutAdmin() }
                        )
                    }
                }
            }
        }
    }
}
