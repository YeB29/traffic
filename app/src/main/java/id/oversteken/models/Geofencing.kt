package id.oversteken.models

import android.content.Context
import android.location.Location
import id.oversteken.data.IVRILocationReader


object Geofencing {
    fun isUserInGeoZone(context: Context, userLocation: Location, radius: Int = 100): Boolean {
        for (coordinate in IVRILocationReader.getIVRILocations(context = context)) {
            val targetLocation = Location("").apply {
                latitude = coordinate.latitude
                longitude = coordinate.longitude
            }
            val distance = userLocation.distanceTo(targetLocation)

            if (distance <= radius) {
                return true
            }
        }
        return false
    }
}