package com.jasatobias.turnosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jasatobias.turnosapp.data.providers.ProviderRepository

import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProviderProfileScreen(
    onProfileSaved: () -> Unit
) {
    val providerRepository = remember {
        ProviderRepository()
    }

    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()

    var businessName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Completa tu perfil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cuéntanos sobre tu negocio",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileField(
                    label = "NOMBRE DEL NEGOCIO",
                    value = businessName,
                    onValueChange = {
                        businessName = it
                        errorMessage = null
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileField(
                    label = "CATEGORÍA",
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = null
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileField(
                    label = "DESCRIPCIÓN",
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = null
                    },
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileField(
                    label = "CIUDAD",
                    value = city,
                    onValueChange = {
                        city = it
                        errorMessage = null
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileField(
                    label = "DIRECCIÓN",
                    value = address,
                    onValueChange = {
                        address = it
                        errorMessage = null
                    }
                )

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {

                        if (
                            businessName.isBlank() ||
                            category.isBlank() ||
                            description.isBlank() ||
                            city.isBlank() ||
                            address.isBlank()
                        ) {
                            errorMessage = "Completa todos los campos"
                            return@Button
                        }

                        val uid = auth.currentUser?.uid

                        if (uid == null) {
                            errorMessage = "No se encontró el usuario"
                            return@Button
                        }

                        scope.launch {

                            isLoading = true
                            errorMessage = null

                            val result = providerRepository.updateProvider(
                                userId = uid,
                                description = description.trim(),
                                city = city.trim(),
                                businessName = businessName.trim(),
                                category = category.trim(),
                                address = address.trim()
                            )

                            isLoading = false

                            result
                                .onSuccess {
                                    onProfileSaved()
                                }
                                .onFailure { error ->
                                    errorMessage =
                                        error.message
                                            ?: "No se pudo guardar el perfil"
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
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = "Guardar perfil",
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
private fun ProfileField(
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

        Spacer(modifier = Modifier.height(4.dp))

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