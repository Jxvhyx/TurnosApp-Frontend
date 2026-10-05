package com.jasatobias.turnosapp.ui.client

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import com.jasatobias.turnosapp.data.services.ServiceRepository
import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue

@Composable
fun ClientServicesScreen(
    onServiceClick: (String) -> Unit
) {

    val serviceRepository = remember { ServiceRepository() }

    var services by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    var searchText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

        isLoading = true
        errorMessage = null

        val result = serviceRepository.getAvailableServices()

        result
            .onSuccess {
                services = it
            }
            .onFailure {
                errorMessage = it.message ?: "No se pudieron cargar los servicios"
            }

        isLoading = false
    }

    val filteredServices = services.filter { service ->

        val name = service["name"] as? String ?: ""

        name.contains(
            searchText,
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = 24.dp
                )
        ) {

            Text(
                text = "Servicios",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Encuentra el servicio que necesitas",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar servicio...")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        when {

            isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
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
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = errorMessage ?: "",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            filteredServices.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (searchText.isBlank()) {
                            "No hay servicios disponibles"
                        } else {
                            "No se encontraron servicios"
                        },
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(filteredServices) { service ->

                        val serviceId =
                            service["id"] as? String ?: ""

                        val name =
                            service["name"] as? String
                                ?: "Servicio"

                        val description =
                            service["description"] as? String
                                ?: ""

                        val duration =
                            (service["duration"] as? Number)?.toInt()
                                ?: 0

                        ServiceCard(
                            name = name,
                            description = description,
                            duration = duration,
                            onClick = {
                                if (serviceId.isNotBlank()) {
                                    onServiceClick(serviceId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    name: String,
    description: String,
    duration: Int,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Duración: $duration minutos",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = RoyalBlue
            )
        }

        Text(
            text = "›",
            fontSize = 30.sp,
            color = RoyalBlue
        )
    }
}
