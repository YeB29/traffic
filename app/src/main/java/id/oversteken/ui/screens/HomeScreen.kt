package id.oversteken.ui.screens

import android.content.Intent
import android.graphics.Paint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import id.oversteken.R
import id.oversteken.data.IVRILocationReader
import id.oversteken.models.ForeGroundLocationState
import id.oversteken.service.ForeGroundLocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon

private lateinit var mapController: IMapController

@Composable
fun HomeScreen(navController: NavHostController) {
    val userLocationState = ForeGroundLocationService.userLocationState.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        Map(userLocationState = userLocationState)

        Overlay(userLocationState = userLocationState)
    }
}

@Composable
fun BoxScope.Overlay(userLocationState: ForeGroundLocationState) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = userLocationState.isInGeoZone,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {

            Icon(Icons.Default.LocationOn, contentDescription = "User Profile")

            Text(
                text = stringResource(id = R.string.location_notification_description_in_radius),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }


    AnimatedContent(
        targetState = userLocationState.serviceRunning,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        when (it) {
            true -> {
                Button(
                    onClick = {
                        context.stopService(
                            Intent(
                                context,
                                ForeGroundLocationService::class.java
                            )
                        )
                    },
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.stop_location_service),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            false -> {
                Button(
                    onClick = {
                        ContextCompat.startForegroundService(
                            context,
                            Intent(
                                context,
                                ForeGroundLocationService::class.java
                            ),
                        )
                    },
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.start_location_service),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Dot in the center of the screen
    AnimatedVisibility(
        visible = userLocationState.serviceRunning,
        modifier = Modifier
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(color = Color.Blue)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape
            )
    ) {
        Box(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun Map(userLocationState: ForeGroundLocationState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // move map with user
    LaunchedEffect(userLocationState.location) {
        if (userLocationState.location.latitude == 0.0 && userLocationState.location.longitude == 0.0) return@LaunchedEffect
        mapController.setCenter(
            GeoPoint(
                userLocationState.location.latitude,
                userLocationState.location.longitude
            )
        )
    }

    AndroidView(
        factory = {
            MapView(context).apply {
                mapController = controller
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(19.0)
                controller.setCenter(
                    GeoPoint(
                        52.312500,
                        5.548611
                    )
                )



                scope.launch(Dispatchers.IO) {
                    val geoZoneColor = android.graphics.Color.argb(
                        60,
                        0,
                        180,
                        255
                    )

                    IVRILocationReader.getIVRILocations(context).forEach {
                        overlayManager.add(Polygon().apply {
                            points = Polygon.pointsAsCircle(
                                GeoPoint(it.latitude, it.longitude), 200.0
                            ) // Radius in meters

                            fillPaint.set(Paint().apply {
                                style = Paint.Style.FILL
                                color = geoZoneColor
                            })

                            outlinePaint.set(Paint().apply {
                                style = Paint.Style.STROKE
                                color = geoZoneColor
                                strokeWidth = 2.0f
                            })
                        })
                    }
                }
            }
        }, modifier = Modifier.fillMaxSize()
    )
}