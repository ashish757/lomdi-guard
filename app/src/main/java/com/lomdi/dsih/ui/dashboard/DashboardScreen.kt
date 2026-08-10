package com.lomdi.dsih.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lomdi.dsih.LomdiAccessibilityService
import com.lomdi.dsih.data.model.ThreatLevel
import com.lomdi.dsih.data.source.*
import com.lomdi.dsih.domain.usecase.RiskManager
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSmsTracker: () -> Unit,
    onNavigateToRemoteApps: () -> Unit,
    onNavigateToClipboard: () -> Unit,
    onNavigateToThreatDetails: () -> Unit,
    onNavigateToAccessibility: () -> Unit,
    onNavigateToTapjacking: () -> Unit,
    onNavigateToKeyboard: () -> Unit,
    onNavigateToCallSecurity: () -> Unit,
    onNavigateToDeviceIntegrity: () -> Unit
) {
    val isRiskCritical = RiskManager.isSystemAtCriticalRisk()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                // Trigger recomposition if needed via state elsewhere
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anti Lomdi Security Hub", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Hero Header (Live System Shield)
            item {
                HeroHeader(
                    isCritical = isRiskCritical,
                    onClick = onNavigateToThreatDetails
                )
            }

            // 2. Category 1: App & System Security
            item {
                CategorySection(title = "App & System Security", icon = Icons.Default.Security) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val trojanCount = AppSecurityStore.activeAccessibilityTrojans.size
                        SecurityModuleCard(
                            title = "Accessibility Auditor",
                            status = if (trojanCount == 0) "0 Trojans" else "⚠️ $trojanCount Trojans Detected",
                            isError = trojanCount > 0,
                            onClick = onNavigateToAccessibility
                        )
                        val overlayCount = AppSecurityStore.activeOverlayApps.size
                        SecurityModuleCard(
                            title = "Tapjacking Auditor",
                            status = if (overlayCount == 0) "Clean" else "⚠️ $overlayCount Overlay Apps",
                            isError = overlayCount > 0,
                            onClick = onNavigateToTapjacking
                        )
                        SecurityModuleCard(
                            title = "Keyboard Auditor",
                            status = if (!AppSecurityStore.isUntrustedKeyboard) "Trusted" else "⚠️ Untrusted IME",
                            isError = AppSecurityStore.isUntrustedKeyboard,
                            onClick = onNavigateToKeyboard
                        )
                    }
                }
            }

            // 3. Category 2: Communication Security
            item {
                CategorySection(title = "Communication Security", icon = Icons.Default.Phone) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecurityModuleCard(
                            title = "Call & Vishing Interceptor",
                            status = if (CallStateStore.isActiveCall || CallStateStore.isVoipCallActive) "⚠️ Active Call" else "Monitoring Calls",
                            isError = (CallStateStore.isActiveCall && CallStateStore.isUnsavedNumber) || (CallStateStore.isVoipCallActive && CallStateStore.isUnsavedVoipCaller),
                            onClick = onNavigateToCallSecurity
                        )
                        SecurityModuleCard(
                            title = "SMS & Link Parser",
                            status = "0 Suspicious Messages",
                            isError = false,
                            onClick = onNavigateToSmsTracker
                        )
                    }
                }
            }

            // 4. Category 3: Real-Time Session Monitors
            item {
                CategorySection(title = "Real-Time Session Monitors", icon = Icons.Default.EmergencyRecording) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecurityModuleCard(
                            title = "Clipboard Guard",
                            status = if (ClipboardStore.hasSensitiveData) "⚠️ UPI Intercepted" else "No Sensitive Data",
                            isError = ClipboardStore.hasSensitiveData,
                            onClick = onNavigateToClipboard
                        )
                        SecurityModuleCard(
                            title = "Remote Apps & Screen Cast",
                            status = if (RemoteAppStore.hasRemoteApps || DeviceSecurityStore.isActiveScreenShare) "⚠️ Threat Active" else "No Active Cast",
                            isError = RemoteAppStore.hasRemoteApps || DeviceSecurityStore.isActiveScreenShare,
                            onClick = onNavigateToRemoteApps
                        )
                    }
                }
            }

            // 5. Category 4: Device Integrity & Tools
            item {
                CategorySection(title = "Device Integrity & Tools", icon = Icons.Default.Build) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecurityModuleCard(
                            title = "Root & Jailbreak Scanner",
                            status = if (DeviceSecurityStore.isDeviceRooted) "⚠️ Rooted" else "Device Intact",
                            isError = DeviceSecurityStore.isDeviceRooted,
                            onClick = onNavigateToDeviceIntegrity
                        )
                    }
                }
            }

            // 6. Recent Interceptions
            item {
                Text(
                    text = "Recent Interceptions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (ThreatLogStore.logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No threats logged yet.", color = Color.Gray)
                    }
                }
            } else {
                items(ThreatLogStore.logs) { log ->
                    ThreatLogItem(log, onClick = onNavigateToThreatDetails)
                }
            }
        }
    }
}

@Composable
fun HeroHeader(isCritical: Boolean, onClick: () -> Unit) {
    val color = if (isCritical) Color(0xFFB71C1C) else Color(0xFF388E3C)
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isCritical) "CRITICAL THREAT DETECTED" else "SYSTEM SECURE",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (isCritical) "Your device is currently at risk" else "Lomdi Shield is actively protecting you",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = if (isCritical) Icons.Default.ReportProblem else Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var simulateEnabled by remember { mutableStateOf(DemoModeStore.isDemoModeEnabled) }
                Text("Simulate Threat", color = Color.White, style = MaterialTheme.typography.labelLarge)
                Switch(
                    checked = simulateEnabled,
                    onCheckedChange = { 
                        simulateEnabled = it
                        DemoModeStore.isDemoModeEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

@Composable
fun CategorySection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
fun SecurityModuleCard(title: String, status: String, isError: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Surface(
                color = if (isError) Color(0xFFB71C1C) else Color(0xFF388E3C).copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isError) Color.White else Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ThreatLogItem(log: ThreatLog, onClick: () -> Unit) {
    val color = when (log.level) {
        ThreatLevel.CRITICAL -> Color(0xFFB71C1C)
        ThreatLevel.HIGH -> Color(0xFFD32F2F)
        else -> MaterialTheme.colorScheme.primary
    }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val improvedType = when(log.type) {
                    "Payment App Launch Intercepted" -> "Suspicious Transaction Halted"
                    "Payment App Threat Detected" -> "Unauthorized Access Blocked"
                    else -> log.type
                }
                Text(text = improvedType, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = "Detected at ${dateFormat.format(log.timestamp)}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = log.level.label, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                Text(text = "Risk Index: ${log.score}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
