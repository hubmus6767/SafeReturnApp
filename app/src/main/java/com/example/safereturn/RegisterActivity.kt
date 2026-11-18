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
import java.util.concurrent.Executor

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricExecutor: Executor
    private var pendingEmail = ""
    private var pendingPassword = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        biometricExecutor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, biometricExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result:
                    BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    val prefs = getSharedPreferences(
                        "SafeReturnPrefs",
                        MODE_PRIVATE
                    )
                    prefs.edit()
                        .putString("saved_email", pendingEmail)
                        .putString("saved_password", pendingPassword)
                        .putBoolean("biometric_enabled", true)
                        .apply()
                    Toast.makeText(
                        applicationContext,
                        "Rejestracja i biometria zakończone sukcesem!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }

                override fun onAuthenticationError(
                    errorCode: Int, errString:
                    CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Błąd biometrii:$errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Nie rozpoznano odcisku palca",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Rejestracja biometryczna SafeReturn")
            .setSubtitle("Zeskanuj odcisk palca, aby powiązać go z kontem")
            .setNegativeButtonText("Anuluj")
            .build()
        btnRegister.setOnClickListener {
            pendingEmail = etEmail.text.toString().trim()
            pendingPassword = etPassword.text.toString().trim()
            if (pendingEmail.isNotEmpty() && pendingPassword.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(pendingEmail, pendingPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            Toast.makeText(
                                this,
                                "Błąd rejestracji: $ {task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this, "Wypełnij wszystkie pola",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}