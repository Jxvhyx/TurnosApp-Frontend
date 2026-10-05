package com.jasatobias.turnosapp.ui.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import com.jasatobias.turnosapp.data.appointments.AppointmentRepository
import com.jasatobias.turnosapp.ui.theme.NavyBackground
import com.jasatobias.turnosapp.ui.theme.RoyalBlue
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Composable
fun AppointmentBookingScreen(
    serviceId: String,
    providerId: String,
    serviceName: String,
    duration: Int,
    onBack: () -> Unit,
    onAppointmentCreated: () -> Unit
) {

    val appointmentRepository = remember {
        AppointmentRepository()
    }

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val scope = rememberCoroutineScope()

    var date by remember {
        mutableStateOf("")
    }

    var startTime by remember {
        mutableStateOf("")
    }

    var endTime by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var successMessage by remember {
        mutableStateOf<String?>(null)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    fun calculateEndTime(
        start: String,
        durationMinutes: Int
    ): String? {

        val parts = start.split(":")

        if (parts.size != 2) {
            return null
        }

        val hour = parts[0].toIntOrNull()
        val minute = parts[1].toIntOrNull()

        if (hour == null || minute == null) {
            return null
        }

        if (hour !in 0..23 || minute !in 0..59) {
            return null
        }

        val totalMinutes =
            hour * 60 + minute + durationMinutes

        if (totalMinutes >= 24 * 60) {
            return null
        }

        val endHour = totalMinutes / 60
        val endMinute = totalMinutes % 60

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            endHour,
            endMinute
        )
    }

    fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->

                date = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )

                errorMessage = null
            },
            year,
            month,
            day
        )

        datePicker.datePicker.minDate =
            calendar.timeInMillis

        datePicker.show()
    }

    fun showTimePicker() {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->

                startTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )

                endTime = calculateEndTime(
                    startTime,
                    duration
                ) ?: ""

                errorMessage = null
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(24.dp)
    ) {

        Text(
            text = "‹ Volver",
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable {
                    onBack()
                },
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = RoyalBlue
        )

        Text(
            text = "Agendar turno",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = serviceName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Duración: $duration minutos",
            fontSize = 14.sp,
            color = Color.LightGray
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker()
                    }
            ) {

                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Fecha")
                    },
                    placeholder = {
                        Text("Selecciona una fecha")
                    },
                    readOnly = true,
                    singleLine = true,
                    enabled = false
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePicker()
                    }
            ) {

                OutlinedTextField(
                    value = startTime,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Hora de inicio")
                    },
                    placeholder = {
                        Text("Selecciona una hora")
                    },
                    readOnly = true,
                    singleLine = true,
                    enabled = false
                )
            }

            /*
             * HORA DE FINALIZACIÓN
             */
            OutlinedTextField(
                value = endTime,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Hora de finalización")
                },
                placeholder = {
                    Text("Se calculará automáticamente")
                },
                readOnly = true,
                singleLine = true,
                enabled = false
            )

            if (errorMessage != null) {

                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            if (successMessage != null) {

                Text(
                    text = successMessage ?: "",
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {

                    val clientId =
                        auth.currentUser?.uid

                    if (clientId == null) {

                        errorMessage =
                            "No hay un usuario autenticado."

                        return@Button
                    }

                    if (date.isBlank()) {

                        errorMessage =
                            "Selecciona una fecha."

                        return@Button
                    }

                    if (startTime.isBlank()) {

                        errorMessage =
                            "Selecciona una hora de inicio."

                        return@Button
                    }

                    val calculatedEndTime =
                        calculateEndTime(
                            startTime,
                            duration
                        )

                    if (calculatedEndTime == null) {

                        errorMessage =
                            "No se pudo calcular la hora de finalización."

                        return@Button
                    }

                    endTime = calculatedEndTime
                    isLoading = true
                    errorMessage = null

                    scope.launch {

                        val result =
                            appointmentRepository.createAppointment(
                                clientId = clientId,
                                providerId = providerId,
                                serviceId = serviceId,
                                date = date,
                                startTime = startTime,
                                endTime = calculatedEndTime
                            )

                        isLoading = false

                        result
                            .onSuccess {

                                successMessage =
                                    "Turno creado correctamente."

                                onAppointmentCreated()
                            }
                            .onFailure { exception ->

                                errorMessage =
                                    exception.message
                                        ?: "No se pudo crear el turno."
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.height(22.dp)
                    )

                } else {

                    Text(
                        text = "Confirmar turno",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
