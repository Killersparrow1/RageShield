package com.milas.rageshield

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.milas.rageshield.accessibility.RageAccessibilityService
import java.text.DateFormat
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RageShieldHome(
                        context = this,
                        onOpenAccessibilitySettings = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onTestSwipe = {
                            RageAccessibilityService.requestTestSwipe()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RageShieldHome(
    context: Context,
    onOpenAccessibilitySettings: () -> Unit,
    onTestSwipe: () -> Boolean
) {
    var serviceEnabled by remember { mutableStateOf(isServiceEnabled(context)) }
    var facebookSeen by remember { mutableStateOf(false) }
    var lastSeenText by remember { mutableStateOf("No Facebook event detected yet") }
    var instagramSeen by remember { mutableStateOf(false) }
    var instagramSeenText by remember { mutableStateOf("No Instagram event detected yet") }
    var result by remember { mutableStateOf("Test swipe has not been run") }
    var testModeAcknowledged by remember { mutableStateOf(false) }

    fun refreshStatus() {
        serviceEnabled = isServiceEnabled(context)
        val prefs = context.getSharedPreferences("rageshield_status", Context.MODE_PRIVATE)
        val timestamp = prefs.getLong(RageAccessibilityService.KEY_LAST_FACEBOOK_EVENT, 0L)
        facebookSeen = timestamp > 0L && System.currentTimeMillis() - timestamp < 15_000L
        lastSeenText = if (timestamp > 0L) {
            "Last Facebook event: " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(timestamp))
        } else {
            "No Facebook event detected yet"
        }

        val igTimestamp = prefs.getLong(RageAccessibilityService.KEY_LAST_INSTAGRAM_EVENT, 0L)
        instagramSeen = igTimestamp > 0L && System.currentTimeMillis() - igTimestamp < 15_000L
        instagramSeenText = if (igTimestamp > 0L) {
            "Last Instagram event: " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(igTimestamp))
        } else {
            "No Instagram event detected yet"
        }
    }

    LaunchedEffect(Unit) { refreshStatus() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("RageShield", style = MaterialTheme.typography.headlineLarge)
        Text("A local-first content-filtering assistant")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Accessibility service")
                Text(if (serviceEnabled) "Enabled" else "Not enabled")
                Button(onClick = onOpenAccessibilitySettings, modifier = Modifier.fillMaxWidth()) {
                    Text("Open Accessibility Settings")
                }
                OutlinedButton(onClick = { refreshStatus() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Refresh status")
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Facebook detection")
                Text(if (facebookSeen) "Facebook activity detected recently" else "Waiting for Facebook activity")
                Text(lastSeenText, style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Instagram detection")
                Text(if (instagramSeen) "Instagram activity detected recently" else "Waiting for Instagram activity")
                Text(instagramSeenText, style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Swipe test (manual)")
                Text("Open Facebook and navigate to a video/Reel. Then return here only if you can do so without losing the screen, or use the accessibility shortcut/test workflow during development.")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = testModeAcknowledged, onCheckedChange = { testModeAcknowledged = it })
                    Spacer(Modifier.height(1.dp))
                    Text("I understand this sends a swipe gesture")
                }
                Button(
                    enabled = serviceEnabled && testModeAcknowledged,
                    onClick = {
                        result = if (onTestSwipe()) {
                            "Swipe request sent. Check the target screen."
                        } else {
                            "Service is not connected. Re-enable it in Accessibility Settings."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Send test swipe") }
                Text(result, style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Planned filter configuration")
                Text("Languages: Malayalam, English, Hindi, Tamil")
                Text("Initial threshold: 90 / 100")
                Text("Automatic filtering: OFF in Phase 1")
            }
        }

        Text(
            "Privacy: this test build records only the latest Facebook accessibility-event time. It does not capture screenshots, read captions, record audio or classify content.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun isServiceEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabled = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    return enabled.any {
        it.resolveInfo.serviceInfo.packageName == context.packageName &&
            it.resolveInfo.serviceInfo.name == RageAccessibilityService::class.java.name
    }
}
