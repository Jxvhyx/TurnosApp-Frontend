package com.jasatobias.turnosapp.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.jasatobias.turnosapp.data.services.ServiceRepository

import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

@Composable
fun CreateServiceScreen(
    onServiceCreated: () -> Unit,
    onBack: () -> Unit
) {

    val serviceRepository = remember { ServiceRepository()}
    val auth = remember {FirebaseAuth.getInstance()}

    val scope = rememberCoroutineScope()

    var name by remember {mutableStateOf("")}
    var description by remember {mutableStateOf("")}
    var duration by remember {mutableStateOf("")}

    var isLoading by remember {mutableStateOf(false) }
    var errorMessage by remember {mutableStateOf<String?>(null)}

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
                    text = "Nuevo servicio",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Agrega un servicio que ofrezcas",
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
                    modifier = Modifier.height(14.dp)
                )

                ServiceField(
                    label = "DESCRIPCIÓN",
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = null
                    },
                    singleLine = false
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                ServiceField(
                    label = "DURACIÓN EN MINUTOS",
                    value = duration,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            duration = value
                            errorMessage = null
                        }
                    }
                )

                errorMessage?.let { message ->

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = {

                        if (name.isBlank()) {
                            errorMessage = "Ingresa el nombre del servicio"
                            return@Button
                        }
                        if (description.isBlank()) {
                            errorMessage = "Ingresa una descripción"
                            return@Button
                        }
                        if (duration.isBlank()) {
                            errorMessage = "Ingresa la duración"
                            return@Button
                        }

                        val durationValue = duration.toIntOrNull()

                        if (durationValue == null || durationValue <= 0) {
                            errorMessage =
                                "La duración debe ser mayor a 0 minutos"
                            return@Button
                        }

                        val providerId = auth.currentUser?.uid

                        if (providerId == null) {
                            errorMessage =
                                "No se encontró el usuario autenticado"
                            return@Button
                        }

                        scope.launch {

                            isLoading = true
                            errorMessage = null

                            val result = serviceRepository.createService(
                                providerId = providerId,
                                name = name.trim(),
                                description = description.trim(),
                                duration = durationValue
                            )

                            isLoading = false

                            result
                                .onSuccess {
                                    onServiceCreated()
                                }
                                .onFailure { error ->
                                    errorMessage =
                                        error.message
                                            ?: "No se pudo crear el servicio"
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue
                    ),
                    enabled = !isLoading
                ) {

                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = "Crear servicio",
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
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = RoyalBlue
            )
        )
    }
}

