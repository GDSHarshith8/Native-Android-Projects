package com.profileviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.profileviewer.Presentation.Navigation.PVNavGraph
import com.profileviewer.Presentation.ViewModel.ProfileViewModel
import com.profileviewer.ui.theme.ProfileViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProfileViewerTheme {
                val viewModel: ProfileViewModel = viewModel()
                PVNavGraph(viewModel)
            }
        }
    }
}