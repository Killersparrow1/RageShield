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
import com.milas.rageshield.core.FilterListRepository
import com.milas.rageshield.core.PlatformRegistry
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
    var platformStatus by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var listStatus by remember { mutableStateOf("") }
    var listBusy by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("Test swipe has not been run") }
    var testModeAcknowledged by remember { mutableStateOf(false) }

    fun refreshStatus() {
        serviceEnabled = isServiceEnabled(context)
        val prefs = context.getSharedPreferences("rageshield_status", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        platformStatus = PlatformRegistry.adapters.map { adapter ->
            val last = adapter.supportedPackageNames
                .maxOfOrNull { prefs.getLong(RageAccessibilityService.statusKey(it), 0L) } ?: 0L
            val text = when {
                last <= 0L -> "No activity yet"
                now - last < 15_000L -> "Active now"
                else -> "Last seen " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(last))
            }
            adapter.platformName to text
        }
    }

    fun refreshListStatus(error: String? = null) {
        val t = FilterListRepository.lastUpdated(context)
        val updated = if (t > 0L) "Updated " + DateFormat.getDateTimeInstance().format(Date(t)) else "Not downloaded yet"
        listStatus = "${FilterListRepository.keywords.size} words total, ${FilterListRepository.communityCount} from community. $updated" +
            (if (error != null) "\nUpdate failed: $error" else "")
    }

    LaunchedEffect(Unit) {
        FilterListRepository.init(context)
        refreshStatus()
        refreshListStatus()
    }

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
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Supported platforms")
                platformStatus.forEach { (name, status) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name)
                        Text(status, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Community filter list")
                Text(listStatus, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Add your own words by editing keywords.txt in the RageShield-FilterList repository on GitHub.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedButton(
                    enabled = !listBusy,
                    onClick = {
                        listBusy = true
                        FilterListRepository.refresh(context) { error ->
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                listBusy = false
                                refreshListStatus(error)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (listBusy) "Updating..." else "Update filter list") }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Swipe test (manual)")
                Text("Open a supported app and navigate to a video/Reel. Then return here only if you can do so without losing the screen, or use the accessibility shortcut/test workflow during development.")
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
            "Privacy: post text is analysed on your phone and never uploaded. The app only downloads the public filter list.",
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
