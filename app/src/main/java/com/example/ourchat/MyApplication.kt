package com.example.ourchat

import android.app.Activity
import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import com.example.ourchat.Utils.ConnectionChangeEvent
import org.greenrobot.eventbus.EventBus


open class MyApplication : Application() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    private var appJustStarted = true

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity) {
                if (::networkCallback.isInitialized) {
                    connectivityManager.unregisterNetworkCallback(networkCallback)

                }
            }

            override fun onActivityResumed(activity: Activity) {
                registerNetworkCallback()
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }


        })
    }


    //Detect network state changes api>=21
    fun registerNetworkCallback() {
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                EventBus.getDefault()
                    .post(ConnectionChangeEvent("sInternet connection lost, Changes will be saved once connection is restored"))

            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (appJustStarted) {
                    appJustStarted = false
                } else {
                    EventBus.getDefault().post(ConnectionChangeEvent("Network is restored."))
                }

            }

        }
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build(),
            networkCallback
        )


    }


}