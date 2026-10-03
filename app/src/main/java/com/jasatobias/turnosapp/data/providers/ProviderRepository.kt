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
}