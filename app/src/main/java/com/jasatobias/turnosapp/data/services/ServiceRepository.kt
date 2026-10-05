package com.jasatobias.turnosapp.data.services

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ServiceRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createService(
        providerId: String,
        name: String,
        description: String,
        duration: Int
    ): Result<Unit>{
        return try{
            val serviceData = hashMapOf(
                "providerId" to providerId,
                "name" to name,
                "description" to description,
                "duration" to duration,
                "available" to true,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )

            firestore
                .collection("Services")
                .add(serviceData)
                .await()

            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getProviderServices(
        providerId: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore
                .collection("Services")
                .whereEqualTo("providerId", providerId)
                .get()
                .await()

            val services = snapshot.documents.map { document ->
                val data = document.data?.toMutableMap() ?: mutableMapOf()

                data["id"] = document.id

                data
            }

            Result.success(services)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getService(
        serviceId: String
    ): Result<Map<String, Any>> {
        return try {
            val document = firestore
                .collection("Services")
                .document(serviceId)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró el servicio")
            }

            val data = document.data?.toMutableMap() ?: mutableMapOf()
            data["id"] = document.id

            Result.success(data)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableServices(): Result<List<Map<String, Any>>> {
        return try {

            val snapshot = firestore
                .collection("Services")
                .whereEqualTo("available", true)
                .get()
                .await()

            val services = snapshot.documents.map { document ->

                val data =
                    document.data?.toMutableMap()
                        ?: mutableMapOf()

                data["id"] = document.id

                data
            }

            Result.success(services)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateService(
        serviceId: String,
        name: String,
        description: String,
        duration: Int
    ): Result<Unit> {
        return try {
            val serviceData = hashMapOf<String, Any>(
                "name" to name,
                "description" to description,
                "duration" to duration,
                "updatedAt" to Timestamp.now()
            )

            firestore
                .collection("Services")
                .document(serviceId)
                .update(serviceData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateServiceAvailability(
        serviceId: String,
        available: Boolean
    ): Result<Unit> {
        return try {
            val serviceData = hashMapOf<String, Any>(
                "available" to available,
                "updatedAt" to Timestamp.now()
            )

            firestore
                .collection("Services")
                .document(serviceId)
                .update(serviceData)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteService(
        serviceId: String
    ): Result<Unit> {
        return try {
            firestore
                .collection("Services")
                .document(serviceId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}