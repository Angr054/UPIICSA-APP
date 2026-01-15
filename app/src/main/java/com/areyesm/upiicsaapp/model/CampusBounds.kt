package com.areyesm.upiicsaapp.model

data class CampusBounds(
    val latNorth: Double,
    val latSouth: Double,
    val lngWest: Double,
    val lngEast: Double
)

val UPIICSA_BOUNDS = CampusBounds(
    latNorth = 19.397350,
    latSouth = 19.393718,
    lngWest  = -99.094775,
    lngEast  = -99.089866
)
