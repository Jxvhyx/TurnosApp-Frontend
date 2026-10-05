package com.jasatobias.turnosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.jasatobias.turnosapp.data.auth.AuthRepository
import com.jasatobias.turnosapp.data.users.UserRepository
import com.jasatobias.turnosapp.ui.HomeScreenGeneral
import com.jasatobias.turnosapp.ui.auth.LoginScreen
import com.jasatobias.turnosapp.ui.MyTicketsScreen
import com.jasatobias.turnosapp.ui.auth.ProviderProfileScreen
import com.jasatobias.turnosapp.ui.auth.RegisterScreen
import com.jasatobias.turnosapp.ui.client.ClientServicesScreen
import com.jasatobias.turnosapp.ui.client.ServiceDetailScreen
import com.jasatobias.turnosapp.ui.services.CreateServiceScreen
import com.jasatobias.turnosapp.ui.services.EditServiceScreen
import com.jasatobias.turnosapp.ui.services.ProviderServicesScreen

import com.jasatobias.turnosapp.ui.theme.TurnosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurnosAppTheme {
                val authRepository = remember { AuthRepository() }
                var isLoggedIn by remember { mutableStateOf(authRepository.isUserLoggedIn()) }

                val userRepository = remember { UserRepository() }
                var currentUserRole by remember { mutableStateOf<String?>(null) }

                var showRegister by remember { mutableStateOf(false) }
                var showProviderProfile by remember { mutableStateOf(false) }
                var showEditProviderProfile by remember { mutableStateOf(false) }
                var showCreateService by remember { mutableStateOf(false) }
                var showProviderServices by remember { mutableStateOf(false) }
                var showEditService by remember { mutableStateOf(false) }
                var selectedService by remember { mutableStateOf<Map<String, Any>?>(null) }
                var showClientServices by remember { mutableStateOf(false) }
                var showServiceDetail by remember { mutableStateOf(false) }
                var selectedServiceId by remember { mutableStateOf<String?>(null) }

                var currentTab by remember { mutableStateOf("home") }
                var servicesRefreshKey by remember { mutableIntStateOf(0) }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        userRepository
                            .getCurrentUser()
                            .onSuccess { userData ->
                                currentUserRole = userData["role"] as? String
                            }
                            .onFailure {
                                currentUserRole = null
                            }
                    } else {
                        currentUserRole = null
                    }
                }

                if (!isLoggedIn) {

                    if (showProviderProfile) {
                        ProviderProfileScreen(
                            onProfileSaved = {
                                showProviderProfile = false
                                isLoggedIn = true
                            }
                        )
                    } else if (showRegister) {
                        RegisterScreen(
                            onRegisterSuccess = { role ->
                                if (role == "provider") {
                                    showProviderProfile = true
                                } else {
                                    isLoggedIn = true
                                }
                            },
                            onBackToLogin = {
                                showRegister = false
                            }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            },
                            onRegisterClick = {
                                showRegister = true
                            }
                        )
                    }

                } else {
                    if (showEditProviderProfile) {
                        ProviderProfileScreen(
                            onProfileSaved = {
                                showEditProviderProfile = false
                            }
                        )
                    } else if (showCreateService) {

                        CreateServiceScreen(

                            onServiceCreated = {
                                showCreateService = false
                                servicesRefreshKey++
                            },
                            onBack = {
                                showCreateService = false
                            }
                        )

                    } else if (showEditService) {

                        val service = selectedService

                        if (service != null) {

                            EditServiceScreen(
                                serviceId = service["id"] as? String ?: "",
                                currentName = service["name"] as? String ?: "",
                                currentDescription = service["description"] as? String ?: "",
                                currentDuration = (service["duration"] as? Number)?.toLong() ?: 0L,

                                onServiceUpdated = {
                                    showEditService = false
                                    selectedService = null
                                    servicesRefreshKey++
                                },

                                onBack = {
                                    showEditService = false
                                    selectedService = null
                                }
                            )

                        }

                    } else if (showProviderServices) {

                        ProviderServicesScreen(

                            refreshKey = servicesRefreshKey,

                            onCreateService = {
                                showCreateService = true
                            },

                            onEditService = { service ->
                                selectedService = service
                                showEditService = true
                            },

                            onBack = {
                                showProviderServices = false
                            }
                        )

                    } else if (showServiceDetail){
                        val serviceId = selectedServiceId

                        if (serviceId != null) {
                            ServiceDetailScreen(
                                serviceId = serviceId,
                                onBack = {
                                    showServiceDetail = false
                                    selectedServiceId = null
                                    showClientServices = true
                                },
                                onBookService = {}
                            )
                        }
                    }
                    else {
                        Scaffold(
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    NavigationBarItem(
                                        selected = currentTab == "home",
                                        onClick = { currentTab = "home" },
                                        icon = { Text("🏠") },
                                        label = { Text("Inicio") }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == "book",
                                        onClick = {
                                            if (currentUserRole == "provider"){
                                                showCreateService = true
                                            } else {
                                                currentTab="book"
                                            }
                                                  },
                                        icon = { Text("➕") },
                                        label = {
                                            if (currentUserRole == "provider") {
                                                Text("Nuevo servicio")
                                            } else {
                                                Text("Agendar")
                                            }
                                        }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == "tickets",
                                        onClick = { currentTab = "tickets" },
                                        icon = { Text("📋") },
                                        label = { Text("Mis Turnos") }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (currentTab) {
                                    "home" -> HomeScreenGeneral(
                                        onNavigateToTab = { tab ->
                                            currentTab = tab
                                        },
                                        onLogout = {
                                            isLoggedIn = false
                                            showRegister = false
                                            showProviderProfile = false
                                            showEditProviderProfile = false
                                            showCreateService = false
                                            showProviderServices = false
                                            showEditService = false
                                            selectedService = null
                                            currentTab = "home"
                                        },
                                        onEditProviderProfile = {
                                            showEditProviderProfile = true
                                        },
                                        onProviderServices = {
                                            showProviderServices = true
                                        }
                                    )
                                    "book" -> {
                                        ClientServicesScreen(
                                            onServiceClick = { serviceId ->
                                                selectedServiceId = serviceId
                                                showServiceDetail = true
                                            }
                                        )
                                    }
                                    "tickets" -> MyTicketsScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}