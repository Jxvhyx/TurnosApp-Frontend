package com.jasatobias.turnosapp.ui.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.jasatobias.turnosapp.data.appointments.AppointmentRepository
import com.jasatobias.turnosapp.data.providers.ProviderRepository
import com.jasatobias.turnosapp.data.services.ServiceRepository
import com.jasatobias.turnosapp.data.users.UserRepository
import com.jasatobias.turnosapp.ui.theme.CardBlue
import com.jasatobias.turnosapp.ui.theme.NavyBackground

@Composable
fun MyTicketsScreen(
    userRole: String
) {

    val appointmentRepository = remember {
        AppointmentRepository()
    }

    val serviceRepository = remember {
        ServiceRepository()
    }

    val providerRepository = remember {
        ProviderRepository()
    }

    val userRepository = remember {
        UserRepository()
    }

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    var appointments by remember {
        mutableStateOf<List<Map<String, Any>>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(userRole) {

        isLoading = true
        errorMessage = null

        val userId = auth.currentUser?.uid

        if (userId == null) {

            errorMessage = "No hay un usuario autenticado."
            isLoading = false

            return@LaunchedEffect
        }

        val result = if (userRole == "provider") {

            appointmentRepository
                .getProviderAppointments(userId)

        } else {

            appointmentRepository
                .getClientAppointments(userId)
        }

        result
            .onSuccess { appointmentList ->

                appointments = appointmentList
            }
            .onFailure { exception ->

                errorMessage =
                    exception.message
                        ?: "No se pudieron cargar los turnos."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(top = 24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mis Turnos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp
            ),
            color = Color.White
        ) {

            when {

                isLoading -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(
                            color = NavyBackground
                        )
                    }
                }

                errorMessage != null -> {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 15.sp
                        )
                    }
                }

                appointments.isEmpty() -> {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text =
                                if (userRole == "provider")
                                    "No tienes turnos registrados."
                                else
                                    "No tienes turnos agendados.",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        item {

                            Text(
                                text =
                                    if (userRole == "provider")
                                        "Turnos de tus clientes"
                                    else
                                        "Tus próximos turnos",

                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyBackground
                            )
                        }

                        items(appointments) { appointment ->

                            AppointmentCard(
                                appointment = appointment,
                                userRole = userRole,
                                serviceRepository =
                                    serviceRepository,
                                providerRepository =
                                    providerRepository,
                                userRepository =
                                    userRepository
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Map<String, Any>,
    userRole: String,
    serviceRepository: ServiceRepository,
    providerRepository: ProviderRepository,
    userRepository: UserRepository
) {

    val serviceId =
        appointment["serviceId"] as? String ?: ""

    val providerId =
        appointment["providerId"] as? String ?: ""

    val clientId =
        appointment["clientId"] as? String ?: ""

    val date =
        appointment["date"] as? String ?: ""

    val startTime =
        appointment["startTime"] as? String ?: ""

    val endTime =
        appointment["endTime"] as? String ?: ""

    val status =
        appointment["status"] as? String ?: ""

    val appointmentId =
        appointment["id"] as? String ?: ""

    var serviceName by remember {
        mutableStateOf("Cargando servicio...")
    }

    var providerName by remember {
        mutableStateOf("")
    }

    var clientName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(appointmentId) {

        if (serviceId.isNotBlank()) {

            serviceRepository
                .getService(serviceId)
                .onSuccess { service ->

                    serviceName =
                        service["name"] as? String
                            ?: "Servicio"
                }
        }

        if (userRole == "client") {

            if (providerId.isNotBlank()) {

                providerRepository
                    .getProvider(providerId)
                    .onSuccess { provider ->

                        providerName =
                            provider["businessName"] as? String
                                ?: "Proveedor"
                    }
            }

        } else {

            if (clientId.isNotBlank()) {

                userRepository
                    .getUser(clientId)
                    .onSuccess { client ->

                        clientName =
                            client["name"] as? String
                                ?: "Cliente"
                    }
            }
        }
    }

    val statusText =
        when (status) {

            "confirmed" -> "● CONFIRMADO"

            "cancelled" -> "● CANCELADO"

            "completed" -> "● COMPLETADO"

            else -> status.uppercase()
        }

    val statusColor =
        when (status) {

            "confirmed" ->
                Color(0xFF16A34A)

            "cancelled" ->
                Color(0xFFDC2626)

            "completed" ->
                Color(0xFF2563EB)

            else ->
                Color(0xFFD97706)
        }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBlue
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "#$appointmentId",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NavyBackground
                )

                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = serviceName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E3A8A)
            )

            if (userRole == "client") {

                Text(
                    text = "Negocio: $providerName",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

            } else {

                Text(
                    text = "Cliente: $clientName",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📅 $date",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Text(
                text = "🕐 $startTime - $endTime",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}