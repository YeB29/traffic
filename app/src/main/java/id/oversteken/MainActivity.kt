package id.oversteken

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import id.oversteken.ui.AppNavigation
import id.oversteken.ui.theme.OverstekenAppTheme
import org.osmdroid.config.Configuration.getInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // OSMDroid
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        super.onCreate(savedInstanceState)
        setContent {
            OverstekenAppTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}