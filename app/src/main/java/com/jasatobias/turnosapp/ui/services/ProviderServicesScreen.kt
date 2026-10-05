package com.jasatobias.turnosapp.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.jasatobias.turnosapp.data.services.ServiceRepository

import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue

@Composable
fun ProviderServicesScreen(
    onCreateService: () -> Unit,
    onEditService: (Map<String, Any>) -> Unit,
    onBack: () -> Unit,
    refreshKey: Int = 0
) {

    val serviceRepository = remember { ServiceRepository() }

    val auth = remember { FirebaseAuth.getInstance() }
    val uid = auth.currentUser?.uid

    var services by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }



    LaunchedEffect(uid, refreshKey) {

        if (uid == null) {
            errorMessage = "No se encontró el usuario"
            isLoading = false
            return@LaunchedEffect
        }

        val result = serviceRepository.getProviderServices(uid)

        result
            .onSuccess { data ->
                services = data
                isLoading = false
            }
            .onFailure { error ->
                errorMessage =
                    error.message ?: "No se pudieron cargar los servicios"

                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 35.dp,
                topEnd = 35.dp
            ),
            color = Color.White
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "‹ Volver",
                    modifier = Modifier.clickable {
                        onBack()
                    },
                    fontSize = 14.sp,
                    color = RoyalBlue
                )

                Text(
                    text = "Mis servicios",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Administra los servicios que ofreces",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                when {
                    isLoading -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = RoyalBlue
                            )
                        }
                    }

                    errorMessage != null -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }
                    }

                    services.isEmpty() -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "Aún no tienes servicios",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBackground
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "Crea tu primer servicio para comenzar",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    else -> {

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                bottom = 16.dp
                            )
                        ) {

                            items(services) { service ->

                                ServiceCard(
                                    name = service["name"] as? String
                                        ?: "Sin nombre",

                                    description = service["description"] as? String
                                        ?: "",

                                    duration = service["duration"] as? Long
                                        ?: 0L,
                                    onEdit = {
                                        onEditService(service)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = onCreateService,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue
                    )
                ) {

                    Text(
                        text = "＋  Nuevo servicio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    name: String,
    description: String,
    duration: Long,
    onEdit: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F5F9)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Text(
                    text = "✏️",
                    modifier = Modifier.clickable {
                        onEdit()
                    },
                    fontSize = 18.sp
                )
            }

            if (description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "⏱",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.size(5.dp)
                )

                Text(
                    text = "$duration minutos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NavyBackground
                )
            }
        }
    }
}