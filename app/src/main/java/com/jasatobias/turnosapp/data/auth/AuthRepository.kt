package com.jasatobias.turnosapp.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ): Result<Unit> {

        return try {

            val authResult = auth
                .createUserWithEmailAndPassword(
                    email,
                    password
                )
                .await()

            val user = authResult.user
                ?: throw Exception("No se pudo obtener el usuario")

            val uid = user.uid

            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "role" to role,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            firestore
                .collection("Users")
                .document(uid)
                .set(userData)
                .await()

            if (role == "provider") {

                val providerData = hashMapOf(
                    "userId" to uid,
                    "description" to "",
                    "city" to "",
                    "businessName" to "",
                    "category" to "",
                    "address" to ""
                )

                firestore
                    .collection("Providers")
                    .document(uid)
                    .set(providerData)
                    .await()
            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}