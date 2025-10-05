package com.literatrack.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ConnectivityMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // 1. Emits the current network status (true/false)
    val isConnected = callbackFlow {
        // Function to check and send the current state
        val checkAndSendStatus = {
            // In ConnectivityMonitor.kt
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            val isOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                    || capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            Log.d("ConnectivityMonitor", "Network status: $isOnline")
            trySend(isOnline) // Send the current status
        }

        // Emit current state immediately when flow is collected
        checkAndSendStatus()

        // The system callback listener
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                checkAndSendStatus() // Network became available
            }

            override fun onLost(network: Network) {
                checkAndSendStatus() // Network was lost
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                checkAndSendStatus() // Capabilities changed (e.g., switched from Wi-Fi to cellular)
            }
        }

        // Register the callback to start listening
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        // When the Flow is cancelled, unregister the callback to prevent leaks
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}