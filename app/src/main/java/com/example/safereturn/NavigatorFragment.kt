package com.example.safereturn

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NavigatorFragment : Fragment(R.layout.fragment_navigator), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // przycisk wylogowania
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        // pobieranie punktow z bazy i dodawanie markerow na mape
        loadWaypointsOnMap()

        // domyslny widok mapy
        val defaultLocation = LatLng(52.237049, 21.017532)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6f))
    }

    private fun loadWaypointsOnMap() {
        db.collection("waypoints")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name") ?: "Punkt terenowy"
                    val lat = document.getDouble("latitude") ?: 0.0
                    val lon = document.getDouble("longitude") ?: 0.0

                    if (lat != 0.0 && lon != 0.0) {
                        val position = LatLng(lat, lon)
                        mMap.addMarker(MarkerOptions().position(position).title(name))
                    }
                }
            }
    }
}