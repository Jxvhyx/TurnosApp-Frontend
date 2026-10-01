package com.jasatobias.turnosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jasatobias.turnosapp.ui.theme.TurnosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurnosAppTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var currentTab by remember { mutableStateOf("home") }

                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = {
                        isLoggedIn = true
                    })
                } else {
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
                                    onClick = { currentTab = "book" },
                                    icon = { Text("➕") },
                                    label = { Text("Agendar") }
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
                                "home" -> HomeScreenGeneral(onNavigateToTab = { tab -> currentTab = tab })
                                "book" -> BookingScreen(onBookingSuccess = { currentTab = "tickets" })
                                "tickets" -> MyTicketsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}