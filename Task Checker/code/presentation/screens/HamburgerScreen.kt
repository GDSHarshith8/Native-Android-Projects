package com.taskchecker.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FilledTonalButton
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay

// step 1: Create an enum that represents the three themes:
enum class AppTheme {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK
}

// step 2:  Create a RememberSavable Shared State for Selected Theme in NG()

// step3 : create HMcontent to Accept and Modify Theme along with other items
@Composable
fun HMcontent(
    drawerState: DrawerState, // ✅ New parameter to control drawer
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onShowOnboarding: () -> Unit // New callback for showing onboarding
) {
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // ✅ To close the drawer

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val drawerWidth = (screenWidthDp * 0.65).dp // 65% of screen width

    ModalDrawerSheet(modifier = Modifier.width(drawerWidth.coerceAtMost(320.dp)) ) {

        DrawerMenuItem(icon = Icons.Default.ColorLens, label = "App Theme") {
            showDialog.value = true
        }

        HorizontalDivider()

        DrawerMenuItem(icon = Icons.Default.Settings, label = "Settings") {
            // TODO: Show Settings screen
        }

        HorizontalDivider()

        // New item to show onboarding again
        DrawerMenuItem(icon = Icons.AutoMirrored.Filled.HelpCenter, label = "How to Use the App") {
            // ✅ Close the drawer first, then trigger onboarding
            coroutineScope.launch {
                drawerState.close()
                delay(100)
                onShowOnboarding()
            }
        }

        HorizontalDivider()

        DrawerMenuItem(icon = Icons.Default.Info, label = "About Dev") {
            // TODO: Show About screen/dialog
        }
    }

    if (showDialog.value) {
        ThemeSelectionDialog(
            selectedTheme = selectedTheme,
            onDismiss = { showDialog.value = false },
            onThemeSelected = {
                onThemeSelected(it)
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
    // Internal state just for dialog selection
    var tempSelection by remember { mutableStateOf(selectedTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose App Theme") },

        text = {
            Column {
                ThemeOptionItem("System Default", AppTheme.SYSTEM_DEFAULT, tempSelection) {
                    tempSelection = it
                }
                ThemeOptionItem("Light Theme", AppTheme.LIGHT, tempSelection) {
                    tempSelection = it
                }
                ThemeOptionItem("Dark Theme", AppTheme.DARK, tempSelection) {
                    tempSelection = it
                }
            }
        },

        confirmButton = {
            FilledTonalButton(onClick = {
                onThemeSelected(tempSelection)
                onDismiss()
            }) {
                Text("Apply")
            }
        },

        dismissButton = {
            FilledTonalButton(onClick = onDismiss) {
                Text("Cancel")
            }
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
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        RadioButton(
            selected = selectedTheme == theme,
            onClick = { onSelect(theme) }
        )
        Spacer(modifier = Modifier.width(6.dp))
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