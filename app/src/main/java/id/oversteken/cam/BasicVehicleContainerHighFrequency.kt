package id.oversteken.cam

data class BasicVehicleContainerHighFreq(
    val Heading: Double,
    val Speed: Double,
    val DriveDirection: Int,
    val VehicleLength: Double,
    val VehicleWidth: Double,
    val LongitudinalAcceleration: Double,
    val Curvature: Double,
    val CurvatureCalculationMode: Int,
    val YawRate: Double,
    val AccelerationControl: Int,
    val LanePosition: Int,
    val SteeringWheelAngle: Double,
    val LateralAcceleration: Double,
    val VerticalAcceleration: Double,
    val PerformanceClass: Int,
    val CenDsrcTollingZone: Int
)