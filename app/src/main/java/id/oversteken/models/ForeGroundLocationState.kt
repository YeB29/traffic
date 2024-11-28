package id.oversteken.models

import android.location.Location


data class ForeGroundLocationState(
    val serviceRunning: Boolean = false,
    val isInGeoZone: Boolean = false,
    val location: Location = Location(null)
)
