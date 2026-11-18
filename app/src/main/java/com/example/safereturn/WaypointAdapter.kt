package com.example.safereturn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaypointAdapter(private val waypoints: List<Waypoint>) :
    RecyclerView.Adapter<WaypointAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvWaypointName)
        val tvCoords: TextView = view.findViewById(R.id.tvWaypointCoords)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_waypoint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val waypoint = waypoints[position]
        holder.tvName.text = waypoint.name
        holder.tvCoords.text = "Lat: ${waypoint.latitude}° N, Lon: ${waypoint.longitude}° E"
    }

    override fun getItemCount(): Int = waypoints.size
}