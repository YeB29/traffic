package id.oversteken.data

import android.content.Context
import id.oversteken.models.IVRILocation
import org.json.JSONArray

object IVRILocationReader {
    private var locations: List<IVRILocation>? = null

    fun getIVRILocations(context: Context): List<IVRILocation> {
        if (locations == null) {
            locations = getIVRILocationsFromAssets(context)
        }
        // double exclamation point as locations can never be null at this point.
        return locations!!
    }

    // Warning! only use when you are 100% sure locations have been set before and you have no way to receive context.
    fun getIVRILocationsUnsafe(): List<IVRILocation>? {
        return locations
    }


    private fun getIVRILocationsFromAssets(context: Context): List<IVRILocation> {
        val locationsFromJSONArray: MutableList<IVRILocation> = mutableListOf()

        try {
            val json = context.assets.open("IVRIData.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val properties = jsonArray.getJSONObject(i).getJSONObject("properties")
                locationsFromJSONArray.add(
                    IVRILocation(
                        properties.getDouble("Latitude"),
                        properties.getDouble("Longitude")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return locationsFromJSONArray.toList()
    }
}