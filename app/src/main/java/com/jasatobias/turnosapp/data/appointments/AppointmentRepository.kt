package com.jasatobias.turnosapp.data.appointments


import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AppointmentRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createAppointment(
        clientId: String,
        providerId: String,
        serviceId: String,
        date: String,
        startTime: String,
        endTime: String
    ): Result<String>{
        return try {
            val appointmentData = hashMapOf(
                "clientId" to clientId,
                "providerId" to providerId,
                "serviceId" to serviceId,
                "date" to date,
                "startTime" to startTime,
                "endTime" to endTime,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )

            val documentReference = firestore
                .collection("Appointments")
                .add(appointmentData)
                .await()

            Result.success(documentReference.id)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getAppointment(
        appointmentId: String
    ): Result<Map<String, Any>>{
        return try {
            val document = firestore
                .collection("Appointments")
                .document(appointmentId)
                .get()
                .await()

            if (!document.exists()){
                throw Exception("No se encontró el turno")
            }

            val data = document.data?.toMutableMap() ?: mutableMapOf()
            data["id"] = document.id

            Result.success(data)
        } catch(e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getClientAppointments(
        clientId: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore
                .collection("Appointments")
                .whereEqualTo("clientId", clientId)
                .get()
                .await()

            val appointments = snapshot.documents.map { document ->
                val data = document.data?.toMutableMap() ?: mutableMapOf()
                data["id"] = document.id
                data
            }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProviderAppointments(
        providerId: String
    ): Result<List<Map<String, Any>>> {
        return try {

            val snapshot = firestore
                .collection("Appointments")
                .whereEqualTo("providerId", providerId)
                .get()
                .await()

            val appointments = snapshot.documents.map { document ->

                val data = document.data?.toMutableMap()
                    ?: mutableMapOf()

                data["id"] = document.id

                data
            }

            Result.success(appointments)

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun updateAppointment(
        appointmentId: String,
        date: String,
        startTime: String,
        endTime: String
    ): Result<Unit> {
        return try {

            val appointmentData = hashMapOf<String, Any>(
                "date" to date,
                "startTime" to startTime,
                "endTime" to endTime,
                "updatedAt" to Timestamp.now()
            )

            firestore
                .collection("Appointments")
                .document(appointmentId)
                .update(appointmentData)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: String
    ): Result<Unit> {
        return try {

            val appointmentData = hashMapOf<String, Any>(
                "status" to status,
                "updatedAt" to Timestamp.now()
            )

            firestore
                .collection("Appointments")
                .document(appointmentId)
                .update(appointmentData)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}