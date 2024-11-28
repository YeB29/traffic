package id.oversteken.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import id.oversteken.R
import id.oversteken.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object FirstPermissionDataStoreManager {
    private val BOOLEAN_KEY = booleanPreferencesKey("first_permission_request")

    suspend fun save(context: Context, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BOOLEAN_KEY] = value
        }
    }

    fun get(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[BOOLEAN_KEY] ?: true
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    navController: NavHostController,
    permissionsState: MultiplePermissionsState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var firstPermissionRequest by remember { mutableStateOf(false) }

    // Collect the saved value from DataStore
    LaunchedEffect(Unit) {
        FirstPermissionDataStoreManager.get(context).collect { value ->
            firstPermissionRequest = value
        }
    }

    Column {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.notification_screen_request_permission_explanation),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), onClick = {
            if (firstPermissionRequest || permissionsState.shouldShowRationale) {
                permissionsState.launchMultiplePermissionRequest()
                scope.launch {
                    FirstPermissionDataStoreManager.save(context, false)
                }
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.setData(uri)
                ContextCompat.startActivity(context, intent, bundleOf())
            }
        }) {
            Text(
                text = if (firstPermissionRequest || permissionsState.shouldShowRationale) {
                    stringResource(id = R.string.notification_screen_request_permission_button)
                } else {
                    stringResource(id = R.string.notification_screen_settings_button)
                }
            )
        }
    }
}