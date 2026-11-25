package com.example.safereturn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvId = view.findViewById<TextView>(R.id.tvId)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            tvId.text = "ID Użytkownika: $uid"
            tvEmail.text = "E-mail: ${currentUser.email ?: "Brak"}"

            // pobranie danych z bazy
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        tvName.text = "Imię i Nazwisko: $firstName $lastName"
                    } else {
                        tvName.text = "Imię i Nazwisko: Nieznane"
                    }
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}