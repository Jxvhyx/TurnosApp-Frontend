package com.jasatobias.turnosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpChatScreen() {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messageList by remember {
        mutableStateOf(
            listOf(
                ChatMessage("¡Hola! Soy tu asistente de IA de TurnosApp. ¿En qué te puedo ayudar hoy con tus citas o servicios?", false)
            )
        )
    }

    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA - TurnosApp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messageList) { message ->
                    ChatBubble(message = message)
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = "Gemini está pensando...",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Pregúntale a la IA sobre tus turnos...") },
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (userInput.isNotBlank() && !isLoading) {
                                val question = userInput
                                userInput = ""
                                messageList = messageList + ChatMessage(question, true)
                                isLoading = true

                                scope.launch {
                                    val aiResponse = getGeminiAIResponse(question)
                                    messageList = messageList + ChatMessage(aiResponse, false)
                                    isLoading = false

                                    if (messageList.isNotEmpty()) {
                                        listState.animateScrollToItem(messageList.size - 1)
                                    }
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val backgroundBubble = if (message.isFromUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromUser) 48.dp else 0.dp,
                end = if (message.isFromUser) 0.dp else 48.dp
            ),
        contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = backgroundBubble,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

// ==========================================
// AQUÍ VA LA FUNCIÓN DE LLAMADA A LA IA
// ==========================================
suspend fun getGeminiAIResponse(query: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-3-flash-preview",
                apiKey = "" // Pega tu clave de Google AI Studio aquí
            )

            val prompt = """
                Eres 'TurnosBot', el asistente virtual experto y oficial de TurnosApp, una aplicación móvil de gestión de citas, servicios y roles.
                Ayuda de forma muy amable, clara y concisa a los usuarios a resolver dudas sobre cómo agendar turnos, cancelar citas o navegar por la aplicación.
                
                Duda del usuario: $query
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "Lo siento, no he podido generar una respuesta en este momento."
        } catch (e: Exception) {
            "Error al conectar con la IA: ${e.localizedMessage ?: "Verifica tu conexión a internet."}"
        }
    }
}