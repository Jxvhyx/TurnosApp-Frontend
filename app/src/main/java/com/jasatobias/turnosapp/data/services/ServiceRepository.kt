package com.jasatobias.turnosapp.data.services

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
                "duration" to duration
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
                "duration" to duration
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
}