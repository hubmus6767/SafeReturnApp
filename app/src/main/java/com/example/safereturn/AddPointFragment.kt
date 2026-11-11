package com.example.safereturn

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class AddPointFragment : Fragment(R.layout.fragment_add_point) {

    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val etName = view.findViewById<EditText>(R.id.etPointName)
        val etDesc = view.findViewById<EditText>(R.id.etPointDesc)
        val switchPrivate = view.findViewById<Switch>(R.id.switchPrivate)
        val btnSave = view.findViewById<Button>(R.id.btnSavePoint)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDesc.text.toString().trim()
            val isPrivate = switchPrivate.isChecked

            if (name.isNotEmpty()) {
                // Przykładowe współrzędne GPS
                val latitude = 49.6214
                val longitude = 20.7042

                val waypointId = db.collection("waypoints").document().id
                val waypoint = Waypoint(
                    id = waypointId,
                    name = name,
                    description = description,
                    latitude = latitude,
                    longitude = longitude,
                    isPrivate = isPrivate
                )
                db.collection("waypoints").document(waypointId)
                .set(waypoint)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Punkt zapisany w chmurze!", Toast.LENGTH_SHORT).show()
                        etName.text.clear()
                        etDesc.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Błąd zapisu: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Wpisz nazwę punktu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}