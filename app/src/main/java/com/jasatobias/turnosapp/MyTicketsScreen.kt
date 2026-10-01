package com.jasatobias.turnosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import com.jasatobias.turnosapp.ui.theme.LightSurface
import com.jasatobias.turnosapp.ui.theme.CardBlue

@Composable
fun MyTicketsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(top = 24.dp)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Turnos Activos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // --- CONTENEDOR BLANCO PRINCIPAL ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Turnos en curso y próximos",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBackground
                    )
                }

                // Tarjeta de Turno 1 (Activo)
                item {
                    TicketCard(
                        code = "TRN-042",
                        service = "Atención al Cliente",
                        branch = "Sede Central",
                        date = "12 Septiembre · 10:30 AM",
                        status = "● CONFIRMADO",
                        statusColor = Color(0xFF16A34A)
                    )
                }

                // Tarjeta de Turno 2 (Pendiente)
                item {
                    TicketCard(
                        code = "TRN-089",
                        service = "Soporte Técnico",
                        branch = "Sede Norte",
                        date = "15 Septiembre · 02:00 PM",
                        status = "⏳ EN ESPERA",
                        statusColor = Color(0xFFD97706)
                    )
                }
            }
        }
    }
}

@Composable
fun TicketCard(code: String, service: String, branch: String, date: String, status: String, statusColor: Color) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$code",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NavyBackground
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = service,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E3A8A)
            )
            Text(
                text = branch,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📅 $date",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}