package com.jasatobias.turnosapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import com.jasatobias.turnosapp.data.auth.AuthRepository
import com.jasatobias.turnosapp.data.users.UserRepository

import com.jasatobias.turnosapp.ui.theme.*

@Composable
fun HomeScreenGeneral(
    onNavigateToTab: (String) -> Unit,
    onLogout: () -> Unit,
    onEditProviderProfile: () -> Unit,
    onProviderServices: () -> Unit
) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository()
    }

    val userRepository = remember {
        UserRepository()
    }

    var userName by remember { mutableStateOf("Usuario") }
    var userRole by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    var isLoadingUser by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        val result = userRepository.getCurrentUser()

        result
            .onSuccess { data ->
                userName = data["name"] as? String ?: "Usuario"
                userRole = data["role"] as? String ?: ""
                userEmail = data["email"] as? String ?: ""
                isLoadingUser = false
            }
            .onFailure {
                userName = "Usuario"
                isLoadingUser = false
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet (drawerContainerColor = Color.White)
            {
                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .clickable {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "👤",
                                fontSize = 30.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBackground
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = if (userRole == "provider")
                                "Prestador de servicio"
                            else
                                "Cliente",
                            fontSize = 12.sp,
                            color = RoyalBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    if (userRole == "provider") {

                        NavigationDrawerItem(
                            icon = {
                                Text("🏢")
                            },
                            label = {
                                Text("Perfil del negocio")
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }

                                onEditProviderProfile()
                            }
                        )
                    }

                    NavigationDrawerItem(
                        icon = {
                            Text("⚙️")
                        },
                        label = {
                            Text("Ajustes")
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    HorizontalDivider()

                    NavigationDrawerItem(
                        icon = {
                            Text("🚪")
                        },
                        label = {
                            Text(
                                "Cerrar sesión",
                                color = Color.Red
                            )
                        },
                        selected = false,
                        onClick = {

                            authRepository.logout()

                            onLogout()
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBackground)
                .padding(top = 24.dp)
        ) {
            // --- 1. CABECERA SUPERIOR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable{
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "¡Hola de nuevo,",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            userName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                IconButton(
                    onClick = { /* Notificaciones */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                ) {
                    Text("🔔", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. CONTENEDOR BLANCO PRINCIPAL ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Tarjeta de bienvenida
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "¿Qué gestión deseas realizar hoy?",
                                fontWeight = FontWeight.Bold,
                                color = NavyBackground,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Solicita turnos y consulta tus reservas activas.",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Acciones rápidas con eventos de clic
                    Text(
                        "Acciones rápidas",
                        fontWeight = FontWeight.Bold,
                        color = NavyBackground,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (userRole == "provider"){

                            QuickActionItem(
                                icon = "🛠️",
                                label = "Mis servicios",
                                onClick = {
                                    onProviderServices()
                                }
                            )

                        } else{

                            QuickActionItem(
                                "➕",
                                "Nuevo Turno"
                            ) { onNavigateToTab("book") }

                            QuickActionItem(
                                "📋",
                                "Mis Turnos"
                            ) { onNavigateToTab("tickets") }
                        }
                        QuickActionItem("🏢", "Sucursales") { /* Acción futura */ }
                        QuickActionItem("⚙️", "Ajustes") { /* Acción futura */ }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Próximo Turno Activo
                    Text(
                        "Próximo turno activo",
                        fontWeight = FontWeight.Bold,
                        color = NavyBackground,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Turno #TRN-042",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Atención al Cliente - Sede Central",
                                    color = Color.DarkGray,
                                    fontSize = 11.sp
                                )
                                Text(
                                    "📅 12 Septiembre · 10:30 AM",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                            Button(
                                onClick = { onNavigateToTab("tickets") }, // Lleva a mis turnos al ver ticket
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Ver Ticket", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Categorías de servicios
                    Text(
                        "Categorías de servicios",
                        fontWeight = FontWeight.Bold,
                        color = NavyBackground,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ServiceItem("💳", "Pagos") { onNavigateToTab("book") }
                        ServiceItem("📄", "Trámites") { onNavigateToTab("book") }
                        ServiceItem("🛠️", "Soporte") { onNavigateToTab("book") }
                        ServiceItem("📦", "Retiros") { onNavigateToTab("book") }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(icon: String, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = Color.DarkGray)
    }
}

@Composable
fun ServiceItem(icon: String, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, color = Color.DarkGray)
    }
}