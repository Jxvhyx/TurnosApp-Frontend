package com.jasatobias.turnosapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit
) {

    val authRepository = remember {
        AuthRepository()
    }

    val scope = rememberCoroutineScope()

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var role by remember {
        mutableStateOf("client")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize(),
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
                    text = "Crear cuenta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Regístrate para comenzar",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // NOMBRE
                Text(
                    text = "NOMBRE",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = RoyalBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // CORREO
                Text(
                    text = "CORREO",
                    modifier = Modifier.fillMaxWidth(),
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
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = RoyalBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // CONTRASEÑA
                Text(
                    text = "CONTRASEÑA",
                    modifier = Modifier.fillMaxWidth(),
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
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = RoyalBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // CONFIRMAR CONTRASEÑA
                Text(
                    text = "CONFIRMAR CONTRASEÑA",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = RoyalBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ROL
                Text(
                    text = "TIPO DE USUARIO",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = role == "client",
                            onClick = {
                                role = "client"
                            }
                        )

                        Text(
                            text = "Cliente",
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = role == "provider",
                            onClick = {
                                role = "provider"
                            }
                        )

                        Text(
                            text = "Prestador",
                            fontSize = 14.sp
                        )
                    }
                }

                errorMessage?.let { message ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {

                        if (
                            name.isBlank() ||
                            email.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            errorMessage = "Completa todos los campos"
                            return@Button
                        }

                        if (password != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden"
                            return@Button
                        }

                        scope.launch {

                            isLoading = true
                            errorMessage = null

                            val result = authRepository.register(
                                name = name.trim(),
                                email = email.trim(),
                                password = password,
                                role = role
                            )

                            isLoading = false

                            result
                                .onSuccess {
                                    onRegisterSuccess(role)
                                }
                                .onFailure { error ->
                                    errorMessage =
                                        error.message
                                            ?: "No se pudo crear la cuenta"
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
                            text = "Registrarse",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onBackToLogin
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta? Inicia sesión",
                        color = RoyalBlue,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}