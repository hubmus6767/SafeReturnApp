package com.example.safereturn

data class Waypoint(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isPrivate: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)