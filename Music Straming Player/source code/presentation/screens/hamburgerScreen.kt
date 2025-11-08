package com.musicplayer.presentation.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.musicplayer.datastore.UserPreferencesViewModel

// step 1: Create an enum that represents the three themes:
enum class AppTheme {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK
}

// step 2: 2. Create a RememberSaveable Shared State for Selected Theme in NG()

// step3 : create HMcontent to Accept and Modify Theme along with other items
@Composable
fun HMcontent(
    userPreferencesViewModel: UserPreferencesViewModel,
    onThemeSelected: (AppTheme) -> Unit,
    drawerState: DrawerState
) {
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val drawerWidth = (screenWidthDp * 0.65).dp // 65% of screen width

    val context = LocalContext.current
    val selectedTheme by userPreferencesViewModel.selectedTheme.collectAsState()

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth.coerceAtMost(320.dp))
    ) {

        DrawerMenuItem(icon = Icons.Default.ColorLens, label = "App Theme") {
            showDialog.value = true
        }

        HorizontalDivider()

        DrawerMenuItem(icon = Icons.Default.Settings, label = "Settings") {
            Toast.makeText(context, "Settings Coming soon!", Toast.LENGTH_SHORT).show()
        }

        HorizontalDivider()

        DrawerMenuItem(icon = Icons.Default.Info, label = "About Dev") {
            val devUrl = "https://github.com/GDSHarshith8"
            val intent = Intent(Intent.ACTION_VIEW, devUrl.toUri())
            context.startActivity(intent)
        }
    }

    if (showDialog.value) {
        ThemeSelectionDialog(
            selectedTheme = selectedTheme,
            onDismiss = { showDialog.value = false },
            onThemeSelected = { theme ->
                onThemeSelected(theme)    // propagate up to MA
                showDialog.value = false
            }
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    selectedTheme: AppTheme,
    onDismiss: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    var tempSelection by remember { mutableStateOf(selectedTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose App Theme") },

        text = {
            Column {
                ThemeOptionItem("System Default", AppTheme.SYSTEM_DEFAULT, tempSelection) { tempSelection = it }
                ThemeOptionItem("Light Theme", AppTheme.LIGHT, tempSelection) { tempSelection = it }
                ThemeOptionItem("Dark Theme", AppTheme.DARK, tempSelection) { tempSelection = it }
            }
        },

        confirmButton = {
            FilledTonalButton(onClick = { onThemeSelected(tempSelection) }) {
                Text("Apply")
            }
        },

        dismissButton = {
            FilledTonalButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// step 4: Create Reusable Radio Option Row
@Composable
fun ThemeOptionItem(
    label: String,
    theme: AppTheme,
    selectedTheme: AppTheme,
    onSelect: (AppTheme) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(theme) }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        RadioButton(
            selected = selectedTheme == theme,
            onClick = { onSelect(theme) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

// step 5: Use the Selected Theme in  MainActivity
// with that theme is over!

// if you want to add more  items in HM :

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label)
    }
}