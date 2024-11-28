package id.oversteken.cam

data class CamParameters(
    val basicContainer: BasicContainer,                     // is verplicht
    val highFrequencyContainer: HighFrequencyContainer,     // is verplicht
    val lowFrequencyContainer: BasicVehicleContainerLowFrequency,
    val specialVehicleContainer: SpecialVehicleContainer?   // dit is conditioneel
)