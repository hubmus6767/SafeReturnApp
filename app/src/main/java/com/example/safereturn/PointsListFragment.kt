package com.example.safereturn

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PointsListFragment : Fragment(R.layout.fragment_points_list) {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerPublic: RecyclerView
    private lateinit var recyclerPrivate: RecyclerView

    private val publicList = mutableListOf<Waypoint>()
    private val privateList = mutableListOf<Waypoint>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        recyclerPublic = view.findViewById(R.id.recyclerPublicPoints)
        recyclerPrivate = view.findViewById(R.id.recyclerPrivatePoints)

        recyclerPublic.layoutManager = LinearLayoutManager(requireContext())
        recyclerPrivate.layoutManager = LinearLayoutManager(requireContext())

        fetchWaypoints()
    }

    private fun fetchWaypoints() {
        db.collection("waypoints")
            .get()
            .addOnSuccessListener { result ->
                publicList.clear()
                privateList.clear()

                for (document in result) {
                    // odczyt danych z bazy Firestone
                    val id = document.getString("id") ?: document.id
                    val userId = document.getString("userId") ?: ""
                    val name = document.getString("name") ?: "Nieznany punkt"
                    val description = document.getString("description") ?: ""
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0

                    // odczyt czy punkt jest prywatny
                    val isPrivate = document.getBoolean("isPrivate")
                        ?: document.getBoolean("private")
                        ?: false

                    val waypoint = Waypoint(
                        id = id,
                        name = name,
                        description = description,
                        latitude = latitude,
                        longitude = longitude,
                        isPrivate = isPrivate
                    )

                    // podzial na liste publiczna i prywatna
                    if (isPrivate) {
                        privateList.add(waypoint)
                    } else {
                        publicList.add(waypoint)
                    }
                }

                // przypisanie adapterow do sekcji
                recyclerPublic.adapter = WaypointAdapter(publicList)
                recyclerPrivate.adapter = WaypointAdapter(privateList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Błąd pobierania punktów: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}