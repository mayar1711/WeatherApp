package com.example.temptrack.checknetowrk


import android.app.Application
import android.net.*
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private const val TAG = "NetworkConnectivity"

class NetworkConnectivity private constructor(val application: Application) {

    private val networkRequest =
        NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

    private val _connectivitySharedFlow = MutableSharedFlow<NetworkStatus>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val connectivitySharedFlow = _connectivitySharedFlow.asSharedFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "onAvailable: ")
            _connectivitySharedFlow.tryEmit(NetworkStatus.CONNECTED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "onLost: ")
            _connectivitySharedFlow.tryEmit(NetworkStatus.LOST)
        }
    }

    init {
        val connectivityManager =
            application.getSystemService(ConnectivityManager::class.java)
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            application.getSystemService(ConnectivityManager::class.java)
        val networkCapabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private lateinit var instance: NetworkConnectivity
        fun getInstance(application: Application): NetworkConnectivity {
            if (!::instance.isInitialized) {
                instance = NetworkConnectivity(application)
            }
            return instance
        }
    }
}
