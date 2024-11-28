package id.oversteken.cam

data class ReferencePosition(
    val Latitude: Double,
    val Longitude: Double,
    val PositionConfidenceEllipse: Int,
    val Altitude: Int                       // verplicht maar wordt niet gebruikt standaar op 0 of null
)