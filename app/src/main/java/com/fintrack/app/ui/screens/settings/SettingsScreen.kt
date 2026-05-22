package com.fintrack.app.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fintrack.app.ui.theme.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("User") }
    var currency by remember { mutableStateOf("PKR") }
    var defaultScreen by remember { mutableStateOf("Dashboard") }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle import logic
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Text("Settings", style = Typography.titleLarge.copy(color = TextPrimary))
        Spacer(modifier = Modifier.height(24.dp))

        // PROFILE
        SettingsSectionTitle("PROFILE")
        SettingsRow(
            title = "Name",
            subtitle = name,
            onClick = { /* Inline editable logic placeholder */ }
        )
        SettingsRow(
            title = "Currency",
            subtitle = currency,
            onClick = { /* Open bottom sheet placeholder */ }
        )
        SettingsRow(
            title = "Default Screen",
            subtitle = defaultScreen,
            onClick = { /* Open bottom sheet placeholder */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // DATA
        SettingsSectionTitle("DATA")
        SettingsRow(
            title = "Export to CSV",
            subtitle = "Generate and share CSV",
            onClick = { viewModel.exportCsv(context) }
        )
        SettingsRow(
            title = "Export to PDF",
            subtitle = "Generate and share PDF",
            onClick = { viewModel.exportPdf(context) }
        )
        SettingsRow(
            title = "Import from CSV",
            subtitle = "Restore data",
            onClick = { importLauncher.launch("text/csv") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ABOUT
        SettingsSectionTitle("ABOUT")
        SettingsRow(
            title = "App Version",
            subtitle = "1.0.0",
            onClick = {}
        )
        SettingsRow(
            title = "Rate App",
            subtitle = "Love the app? Rate us!",
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                    context.startActivity(intent)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = Typography.labelSmall.copy(color = TextSecondary),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(SurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = Typography.bodyLarge.copy(color = TextPrimary))
            Text(subtitle, style = Typography.labelSmall.copy(color = TextSecondary))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextDisabled
        )
    }
    HorizontalDivider(color = BorderColor, thickness = 1.dp)
}
