package com.jasatobias.turnosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.jasatobias.turnosapp.data.auth.AuthRepository

import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {

    val authRepository = remember {
        AuthRepository()
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        // --- CABECERA SUPERIOR CON ICONO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 40.sp)
            }
        }

        // --- CONTENEDOR BLANCO INFERIOR (Curvado) ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f),
            shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Iniciar sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Correo Electrónico
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CORREO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = RoyalBlue
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Contraseña Único
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CONTRASEÑA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = RoyalBlue
                        ),
                        singleLine = true
                    )
                }

                errorMessage?.let { message ->

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Ingresar
                Button(
                    onClick = {

                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Completa todos los campos"
                            return@Button
                        }

                        scope.launch {

                            isLoading = true
                            errorMessage = null

                            val result = authRepository.login(
                                email = email.trim(),
                                password = password
                            )

                            isLoading = false

                            result
                                .onSuccess {
                                    onLoginSuccess()
                                }
                                .onFailure {
                                    errorMessage = "Correo o contraseña incorrectos"
                                }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Ingresar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlaces inferiores
                TextButton(
                    onClick = onRegisterClick
                ) {
                    Text(
                        text = "¡Regístrate!",
                        color = RoyalBlue
                    )
                }
            }
        }
    }
}