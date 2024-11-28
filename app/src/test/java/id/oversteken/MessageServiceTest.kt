//package id.oversteken
//
//import id.oversteken.models.Message
//import junit.framework.TestCase.assertEquals
//import junit.framework.TestCase.assertNotSame
//import org.junit.Test
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
//
//
//class MessageServiceTest {
//
//    lateinit var message: Message
//@Test
//fun BasicContainer_isCorrect() {
//
//    val message = Message()
//    val latitude = 52.5200
//    val longitude = 13.4050
//    val data = message.settingData(latitude, longitude)
//
//    assertEquals(1, data.basicContainer.StationType)
//    assertEquals(52.5200, data.basicContainer.ReferencePosition.Latitude)
//    assertEquals(13.4050, data.basicContainer.ReferencePosition.Longitude)
//    assertEquals(0, data.basicContainer.ReferencePosition.PositionConfidenceEllipse)
//    assertEquals(0, data.basicContainer.ReferencePosition.Altitude)
//}
//@Test
//fun BasicContainer_isFalse() {
//
//    val message = Message()
//    val latitude = 52.5200
//    val longitude = 13.4050
//    val data = message.settingData(latitude, longitude)
//
//    assertNotSame(2, data.basicContainer.StationType)
//    assertNotSame(53.5200, data.basicContainer.ReferencePosition.Latitude)
//    assertNotSame(14.4050, data.basicContainer.ReferencePosition.Longitude)
//    assertNotSame(6, data.basicContainer.ReferencePosition.PositionConfidenceEllipse)
//    assertNotSame(39, data.basicContainer.ReferencePosition.Altitude)
//}
//
//    @Test
//    fun BasicVehicleContainer_isCorrect() {
//
//        val message = Message()
//        val latitude = 52.5200
//        val longitude = 13.4050
//        val data = message.settingData(latitude, longitude)
//
//        assertEquals(100.0, data.basicVehicleContainerHighFreq.Heading)
//        assertEquals(5.0, data.basicVehicleContainerHighFreq.Speed)
//        assertEquals(1, data.basicVehicleContainerHighFreq.DriveDirection)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.VehicleLength)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.VehicleWidth)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.LongitudinalAcceleration)
//        assertEquals(0.1, data.basicVehicleContainerHighFreq.Curvature)
//        assertEquals(1, data.basicVehicleContainerHighFreq.CurvatureCalculationMode)
//        assertEquals(0.01, data.basicVehicleContainerHighFreq.YawRate)
//        assertEquals(0, data.basicVehicleContainerHighFreq.AccelerationControl)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.SteeringWheelAngle)
//        assertEquals(1, data.basicVehicleContainerHighFreq.LanePosition)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.LateralAcceleration)
//        assertEquals(0.0, data.basicVehicleContainerHighFreq.VerticalAcceleration)
//        assertEquals(1, data.basicVehicleContainerHighFreq.PerformanceClass)
//        assertEquals(0, data.basicVehicleContainerHighFreq.CenDsrcTollingZone)
//    }
//
//
//
//}