package com.example.safereturn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executor

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var pendingEmail = ""
    private var pendingPassword = ""
    private var pendingFirstName = ""
    private var pendingLastName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userId = currentUser.uid // Automatycznie generowane ID przez Firebase

                        val userMap = hashMapOf(
                            "uid" to userId,
                            "firstName" to pendingFirstName,
                            "lastName" to pendingLastName,
                            "email" to pendingEmail
                        )

                        // zapis danych uzytwkonika do bazy
                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                val prefs = getSharedPreferences("SafeReturnPrefs", MODE_PRIVATE)
                                prefs.edit()
                                    .putString("saved_email", pendingEmail)
                                    .putString("saved_password", pendingPassword)
                                    .putBoolean("biometric_enabled", true)
                                    .apply()

                                Toast.makeText(applicationContext, "Konto utworzone pomyślnie!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(applicationContext, "Błąd zapisu profilu: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Błąd biometrii: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Rejestracja SafeReturn")
            .setSubtitle("Potwierdź tożsamość odciskiem palca")
            .setNegativeButtonText("Anuluj")
            .build()

        btnRegister.setOnClickListener {
            pendingFirstName = etFirstName.text.toString().trim()
            pendingLastName = etLastName.text.toString().trim()
            pendingEmail = etEmail.text.toString().trim()
            pendingPassword = etPassword.text.toString().trim()

            if (pendingFirstName.isNotEmpty() && pendingLastName.isNotEmpty() && pendingEmail.isNotEmpty() && pendingPassword.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(pendingEmail, pendingPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            Toast.makeText(this, "Błąd rejestracji: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }
}