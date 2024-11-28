package id.oversteken.cam


// ItsPduHeader.kt
data class PduHeader(
    // Add appropriate fields here
    val protocolVersion : Int,
    val messageID : Int,
    val stationID : Int
       
)