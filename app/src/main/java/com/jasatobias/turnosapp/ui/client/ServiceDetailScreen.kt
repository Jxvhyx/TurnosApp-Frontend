package com.jasatobias.turnosapp.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.jasatobias.turnosapp.data.providers.ProviderRepository
import com.jasatobias.turnosapp.data.services.ServiceRepository
import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit,
    onBookService: (
        serviceId: String,
        providerId: String,
        serviceName: String,
        duration: Int
            ) -> Unit
) {

    val serviceRepository = remember {
        ServiceRepository()
    }

    val providerRepository = remember {
        ProviderRepository()
    }

    var service by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    var provider by remember {
        mutableStateOf<Map<String, Any>?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(serviceId) {

        isLoading = true
        errorMessage = null

        val serviceResult =
            serviceRepository.getService(serviceId)

        serviceResult
            .onSuccess { serviceData ->

                service = serviceData

                val providerId =
                    serviceData["providerId"] as? String

                if (providerId.isNullOrBlank()) {

                    errorMessage =
                        "El servicio no tiene un proveedor asociado."

                } else {

                    val providerResult =
                        providerRepository.getProvider(providerId)

                    providerResult
                        .onSuccess { providerData ->
                            provider = providerData
                        }
                        .onFailure { exception ->
                            errorMessage =
                                exception.message
                                    ?: "No se pudo cargar la información del proveedor"
                        }
                }
            }
            .onFailure { exception ->

                errorMessage =
                    exception.message
                        ?: "No se pudo cargar el servicio"
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = RoyalBlue
                )
            }

        } else if (errorMessage != null) {

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

        } else if (service != null) {

            ServiceDetailContent(
                serviceId = serviceId,
                service = service!!,
                provider = provider,
                onBack = onBack,
                onBookService = onBookService
            )
        }
    }
}

@Composable
private fun ServiceDetailContent(
    serviceId: String,
    service: Map<String, Any>,
    provider: Map<String, Any>?,
    onBack: () -> Unit,
    onBookService: (
        serviceId: String,
        providerId: String,
        serviceName: String,
        duration: Int
    ) -> Unit
) {

    val name =
        service["name"] as? String ?: "Servicio"

    val description =
        service["description"] as? String ?: ""

    val duration =
        (service["duration"] as? Number)?.toInt() ?: 0

    val businessName =
        provider?.get("businessName") as? String
            ?: "Proveedor"

    val category =
        provider?.get("category") as? String
            ?: ""

    val city =
        provider?.get("city") as? String
            ?: ""

    val address =
        provider?.get("address") as? String
            ?: ""

    val providerDescription =
        provider?.get("description") as? String
            ?: ""

    Column(
        modifier = Modifier.fillMaxSize()
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
                text = "‹ Volver",
                modifier = Modifier.clickable {
                    onBack()
                },
                fontSize = 14.sp,
                color = RoyalBlue
            )

            Text(
                text = name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = description,
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            DetailCard(
                title = "Información del servicio"
            ) {

                DetailItem(
                    label = "Duración",
                    value = "$duration minutos"
                )
            }

            DetailCard(
                title = "Información del proveedor"
            ) {

                DetailItem(
                    label = "Negocio",
                    value = businessName
                )

                if (category.isNotBlank()) {

                    DetailItem(
                        label = "Categoría",
                        value = category
                    )
                }

                if (providerDescription.isNotBlank()) {

                    DetailItem(
                        label = "Descripción",
                        value = providerDescription
                    )
                }

                if (city.isNotBlank()) {

                    DetailItem(
                        label = "Ciudad",
                        value = city
                    )
                }

                if (address.isNotBlank()) {

                    DetailItem(
                        label = "Dirección",
                        value = address
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            androidx.compose.material3.Button(
                onClick = {
                    val providerId =
                        service["providerId"] as? String

                    if (!providerId.isNullOrBlank()) {

                        onBookService(
                            serviceId,
                            providerId,
                            name,
                            duration
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = "Agendar turno",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(18.dp)
            )
            .padding(18.dp)
    ) {

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        content()
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = RoyalBlue
        )

        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}

