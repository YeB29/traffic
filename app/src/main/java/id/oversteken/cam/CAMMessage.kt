package id.oversteken.cam

import id.oversteken.cam.PduHeader

data class CooperativeAwarenessMessage(
    val ItsPduHeader: PduHeader,     // is verplicht
    val CoopAwareness: CoopAwareness    // is verplicht
)
