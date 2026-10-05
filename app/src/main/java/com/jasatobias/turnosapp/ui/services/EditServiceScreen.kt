package com.jasatobias.turnosapp.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.jasatobias.turnosapp.data.services.ServiceRepository

import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

@Composable
fun EditServiceScreen(
    serviceId: String,
    currentName: String,
    currentDescription: String,
    currentDuration: Long,
    onServiceUpdated: () -> Unit,
    onBack: () -> Unit
) {

    val serviceRepository = remember { ServiceRepository() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(currentName) }
    var description by remember { mutableStateOf(currentDescription) }
    var duration by remember { mutableStateOf(currentDuration.toString()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Editar servicio",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Modifica la información de tu servicio",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                ServiceField(
                    label = "NOMBRE DEL SERVICIO",
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                ServiceField(
                    label = "DESCRIPCIÓN",
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = null
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                ServiceField(
                    label = "DURACIÓN (MINUTOS)",
                    value = duration,
                    onValueChange = { value ->

                        if (value.all { it.isDigit() }) {
                            duration = value
                            errorMessage = null
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                errorMessage?.let { message ->

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {

                        when {
                            name.isBlank() -> {
                                errorMessage =
                                    "El nombre del servicio es obligatorio"
                            }

                            description.isBlank() -> {
                                errorMessage =
                                    "La descripción es obligatoria"
                            }

                            duration.isBlank() -> {
                                errorMessage =
                                    "La duración es obligatoria"
                            }

                            duration.toIntOrNull() == null -> {
                                errorMessage =
                                    "La duración debe ser un número válido"
                            }

                            duration.toInt() <= 0 -> {
                                errorMessage =
                                    "La duración debe ser mayor a 0"
                            }

                            else -> {

                                isLoading = true
                                errorMessage = null

                                scope.launch {

                                    val result =
                                        serviceRepository.updateService(
                                            serviceId = serviceId,
                                            name = name.trim(),
                                            description = description.trim(),
                                            duration = duration.toInt()
                                        )

                                    result
                                        .onSuccess {
                                            isLoading = false
                                            onServiceUpdated()
                                        }
                                        .onFailure { error ->
                                            isLoading = false
                                            errorMessage =
                                                error.message
                                                    ?: "No se pudo actualizar el servicio"
                                        }
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue
                    )
                ) {

                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = "Guardar cambios",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    Column {

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RoyalBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = RoyalBlue,
                cursorColor = RoyalBlue
            )
        )
    }
}