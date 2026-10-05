package com.jasatobias.turnosapp.data.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUser(): Result<Map<String, Any>> {

        return try {

            val uid = auth.currentUser?.uid
                ?: throw Exception("No hay un usuario autenticado")

            val document = firestore
                .collection("Users")
                .document(uid)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró la información del usuario")
            }

            Result.success(document.data ?: emptyMap())

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getUser(
        userId: String
    ): Result<Map<String, Any>> {
        return try {

            val document = firestore
                .collection("Users")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró el usuario")
            }

            Result.success(
                document.data ?: emptyMap()
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}