package id.oversteken.models

import com.androidfactory.network.KtorClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import id.oversteken.cam.PduHeader
import id.oversteken.cam.BasicContainer
import id.oversteken.cam.BasicVehicleContainerHighFreq
import id.oversteken.cam.BasicVehicleContainerLowFrequency
import id.oversteken.cam.CamParameters
import id.oversteken.cam.CoopAwareness
import id.oversteken.cam.CooperativeAwarenessMessage
import id.oversteken.cam.HighFrequencyContainer
import id.oversteken.cam.RSUContainerHighFrequency
import id.oversteken.cam.ReferencePosition
import id.oversteken.cam.SpecialVehicleContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Message(private val ktorClient: KtorClient) {

    fun sendMessage(latitude: Double, longitude: Double) {
        try {
            val message = createMessage(latitude, longitude)

            CoroutineScope(Dispatchers.IO).launch {
                val result = ktorClient.sendMessage(message)
                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        println("Message sent successfully with response: $it")
                    }.onFailure {
                        println("Failed to send message: ${it.message}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Print the stack trace to the log for debugging
        }
    }

    fun stopMessage(){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val result = ktorClient.stopMessage()
                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        println("Message sent successfully with response: $it")
                    }.onFailure {
                        println("Failed to send message: ${it.message}")
                    }
                }
            }

        }
        catch (e: Exception){
            e.printStackTrace() // Print the stack trace to the log for debugging
        }
    }

    private fun createMessage(latitude: Double, longitude: Double): String {
        val gson = Gson()
        val data = settingData(latitude, longitude)

        val jsonObject = JsonObject().apply {
            add("CAMMessage", JsonObject().apply {
                add("ItsPduHeader", gson.toJsonTree(data.cooperativeAwarenessMessage.ItsPduHeader))
                add("CoopAwareness", JsonObject().apply {
                    addProperty("GenerationDeltaTime", data.coopAwareness.GenerationDeltaTime)
                    add("CamParameters", JsonObject().apply {
                        add("BasicContainer", gson.toJsonTree(data.camParameters.basicContainer))
                        add("HighFrequencyContainer", gson.toJsonTree(data.camParameters.highFrequencyContainer))
                        add("LowFrequencyContainer", gson.toJsonTree(data.camParameters.lowFrequencyContainer))
                        add("SpecialVehicleContainer", gson.toJsonTree(data.camParameters.specialVehicleContainer))
                    })
                })
            })
        }

        return gson.toJson(jsonObject)
    }

    private fun settingData(latitude: Double, longitude: Double): Data {
        val referencePosition = ReferencePosition(
            Latitude = latitude,
            Longitude = longitude,
            PositionConfidenceEllipse = 5,
            Altitude = 34
        )

        val basicContainer = BasicContainer(
            StationType = 1,
            ReferencePosition = referencePosition
        )

        val basicVehicleContainerHighFreq = BasicVehicleContainerHighFreq(
            Heading = 100.0,
            Speed = 5.0,
            DriveDirection = 1,
            VehicleLength = 0.0,
            VehicleWidth = 0.0,
            LongitudinalAcceleration = 0.0,
            Curvature = 0.1,
            CurvatureCalculationMode = 1,
            YawRate = 0.0,
            AccelerationControl = 1,
            LanePosition = 1,
            SteeringWheelAngle = 0.0,
            LateralAcceleration = 0.1,
            VerticalAcceleration = 0.0,
            PerformanceClass = 1,
            CenDsrcTollingZone = 0
        )

        val highFrequencyContainer = HighFrequencyContainer(
        )

        val basicVehicleContainerLowFreq = BasicVehicleContainerLowFrequency(
            VehicleRole = 0,
            ExteriorLights = 0,
            PathHistory = 0
        )

        val camParameters = CamParameters(
            basicContainer = basicContainer,
            highFrequencyContainer = highFrequencyContainer,
            lowFrequencyContainer = basicVehicleContainerLowFreq,
            specialVehicleContainer = null
        )

        val coopAwareness = CoopAwareness(
            GenerationDeltaTime = 10,
            CamParameters = camParameters
        )

        val cooperativeAwarenessMessage = CooperativeAwarenessMessage(
            ItsPduHeader = PduHeader(2, 2, 1),
            CoopAwareness = coopAwareness
        )

        val rsuContainerHighFrequency = RSUContainerHighFrequency(12)

        return Data(
            basicContainer,
            basicVehicleContainerHighFreq,
            basicVehicleContainerLowFreq,
            cooperativeAwarenessMessage,
            camParameters,
            coopAwareness,
            referencePosition,
            highFrequencyContainer,
            rsuContainerHighFrequency,
            null
        )
    }

    data class Data(
        val basicContainer: BasicContainer,
        val basicVehicleContainerHighFreq: BasicVehicleContainerHighFreq,
        val basicVehicleContainerLowFreq: BasicVehicleContainerLowFrequency,
        val cooperativeAwarenessMessage: CooperativeAwarenessMessage,
        val camParameters: CamParameters,
        val coopAwareness: CoopAwareness,
        val referencePosition: ReferencePosition,
        val highFrequencyContainer: HighFrequencyContainer,
        val rsuContainerHighFrequency: RSUContainerHighFrequency,
        val specialVehicleContainer: SpecialVehicleContainer?
    )
}