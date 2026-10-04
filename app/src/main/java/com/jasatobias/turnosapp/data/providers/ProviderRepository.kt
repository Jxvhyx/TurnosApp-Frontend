package com.jasatobias.turnosapp.data.providers

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProviderRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateProvider(
        userId: String,
        description: String,
        city: String,
        businessName: String,
        category: String,
        address: String
    ): Result<Unit> {
        return try {
            val providerData = hashMapOf(
                "userId" to userId,
                "description" to description,
                "city" to city,
                "businessName" to businessName,
                "category" to category,
                "address" to address
            )

            firestore
                .collection("Providers")
                .document(userId)
                .set(providerData)
                .await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProvider(
        userId: String
    ): Result<Map<String, Any>> {
        return try {
            val document = firestore
                .collection("Providers")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró la información del proveedor")
            }

            Result.success(document.data ?: emptyMap())

        } catch (e: Exception){
            Result.failure(e)
        }
    }
}