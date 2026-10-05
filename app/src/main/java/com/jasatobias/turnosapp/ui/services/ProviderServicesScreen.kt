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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.jasatobias.turnosapp.data.services.ServiceRepository
import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProviderServicesScreen(
    onCreateService: () -> Unit,
    onEditService: (Map<String, Any>) -> Unit,
    onBack: () -> Unit,
    refreshKey: Int = 0
) {

    val serviceRepository = remember { ServiceRepository() }
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()

    val uid = auth.currentUser?.uid

    var services by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var actionLoadingId by remember { mutableStateOf<String?>(null) }

    var serviceToDelete by remember { mutableStateOf<Map<String, Any>?>(null) }

    suspend fun loadServices() {

        if (uid == null) {
            errorMessage = "No se encontró el usuario"
            isLoading = false
            return
        }

        isLoading = true
        errorMessage = null

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

    LaunchedEffect(uid, refreshKey) {
        loadServices()
    }

    // Diálogo de confirmación para eliminar
    serviceToDelete?.let { service ->

        val serviceName =
            service["name"] as? String ?: "este servicio"

        AlertDialog(
            onDismissRequest = {
                serviceToDelete = null
            },
            title = {
                Text(
                    text = "Eliminar servicio",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar \"$serviceName\"? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        val serviceId =
                            service["id"] as? String

                        if (serviceId == null) {
                            serviceToDelete = null
                            return@TextButton
                        }

                        scope.launch {

                            actionLoadingId = serviceId
                            serviceToDelete = null

                            val result =
                                serviceRepository.deleteService(serviceId)

                            result
                                .onSuccess {
                                    loadServices()
                                }
                                .onFailure { error ->
                                    errorMessage =
                                        error.message
                                            ?: "No se pudo eliminar el servicio"
                                }

                            actionLoadingId = null
                        }
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        serviceToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
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

                            items(
                                items = services,
                                key = {
                                    it["id"] as? String ?: it.hashCode()
                                }
                            ) { service ->

                                val serviceId =
                                    service["id"] as? String ?: ""

                                val name =
                                    service["name"] as? String
                                        ?: "Sin nombre"

                                val description =
                                    service["description"] as? String
                                        ?: ""

                                val duration =
                                    service["duration"] as? Long
                                        ?: 0L

                                val available =
                                    service["available"] as? Boolean
                                        ?: false

                                val createdAt =
                                    service["createdAt"] as? Timestamp

                                val updatedAt =
                                    service["updatedAt"] as? Timestamp

                                ServiceCard(
                                    name = name,
                                    description = description,
                                    duration = duration,
                                    available = available,
                                    createdAt = createdAt,
                                    updatedAt = updatedAt,
                                    isLoading = actionLoadingId == serviceId,

                                    onEdit = {
                                        onEditService(service)
                                    },

                                    onToggleAvailability = {

                                        if (serviceId.isEmpty()) {
                                            return@ServiceCard
                                        }

                                        scope.launch {

                                            actionLoadingId = serviceId
                                            errorMessage = null

                                            val result =
                                                serviceRepository
                                                    .updateServiceAvailability(
                                                        serviceId = serviceId,
                                                        available = !available
                                                    )

                                            result
                                                .onSuccess {
                                                    loadServices()
                                                }
                                                .onFailure { error ->
                                                    errorMessage =
                                                        error.message
                                                            ?: "No se pudo actualizar el estado"
                                                }

                                            actionLoadingId = null
                                        }
                                    },

                                    onDelete = {
                                        serviceToDelete = service
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
    available: Boolean,
    createdAt: Timestamp?,
    updatedAt: Timestamp?,
    isLoading: Boolean,
    onEdit: () -> Unit,
    onToggleAvailability: () -> Unit,
    onDelete: () -> Unit
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
                        if (!isLoading) {
                            onEdit()
                        }
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

            // Duración
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

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // Estado
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (available) "●" else "●",
                    fontSize = 12.sp,
                    color = if (available) {
                        Color(0xFF16A34A)
                    } else {
                        Color.Gray
                    }
                )

                Spacer(
                    modifier = Modifier.size(6.dp)
                )

                Text(
                    text = if (available) {
                        "Disponible"
                    } else {
                        "Deshabilitado"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (available) {
                        Color(0xFF16A34A)
                    } else {
                        Color.Gray
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // Fechas
            Text(
                text = "Creado: ${formatTimestamp(createdAt)}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "Actualizado: ${formatTimestamp(updatedAt)}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onToggleAvailability,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (available) {
                            Color(0xFFE5E7EB)
                        } else {
                            RoyalBlue
                        }
                    )
                ) {

                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = if (available) {
                                NavyBackground
                            } else {
                                Color.White
                            },
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = if (available) {
                                "Deshabilitar"
                            } else {
                                "Habilitar"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (available) {
                                NavyBackground
                            } else {
                                Color.White
                            }
                        )
                    }
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = Color.Red
                    )
                ) {

                    Text(
                        text = "Eliminar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(
    timestamp: Timestamp?
): String {

    if (timestamp == null) {
        return "No disponible"
    }

    val formatter = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formatter.format(
        timestamp.toDate()
    )
}