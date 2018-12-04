package pl.sviete.dom.devices.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.net.*
import pl.sviete.dom.devices.net.Models.AccessPointInfo

class WiFiScanner (context: Context) {
    private val mContext = context
    private val TAG = WiFiScanner::class.java.simpleName
    private var mWiFiManager: WifiManager? = null
    private var mLocationManager: LocationManager? = null
    private var mWifiScanReceiver: BroadcastReceiver? = null

    fun startScan(listener: OnScanResultsListener) {
        if (mWifiScanReceiver == null) {
            mWifiScanReceiver = object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    if (success) {
                        listener.onScanResults(getScanResult())
                    }
                }
            }

            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            mContext.registerReceiver(mWifiScanReceiver, intentFilter)
        }
        wiFiManager.startScan()
    }

    fun stopScan(){
        if (mWifiScanReceiver != null) {
            mContext.unregisterReceiver(mWifiScanReceiver)
            mWifiScanReceiver = null
        }
    }

    private fun getScanResult(): List<AccessPointInfo>{
        val result = mutableListOf<AccessPointInfo>()
        val scans = wiFiManager.scanResults
        if (scans == null) {
            //  "Failed getting scan results"
        } else if (scans.isEmpty() && !isLocationEnabled()) {
            // https://issuetracker.google.com/issues/37060483:
            // "WifiManager#getScanResults() returns an empty array list if GPS is turned off"
            //  "Location needs to be enabled on the device"
        } else {
            for (scan in scans) {
                if (scan.frequency < 2500) {
                    val ap = AccessPointInfo(scan.SSID, scan.BSSID)
                    result.add(ap)
                }
            }
        }
        return result
    }

    interface OnScanResultsListener {
        fun onScanResults(scanResult: List<AccessPointInfo>)
    }

    fun enableWiFi(enabled: Boolean) {
        try {
            wiFiManager.isWifiEnabled = enabled
        } catch (e: Exception) {
            Log.e(TAG, "onReceiveWifiEnable $e")
        }
    }

    fun getCurrentNetworkId(): Int{
        val info = wiFiManager.connectionInfo
        return info.networkId
    }

    fun removeSsid(ssid: String){
        val list = wiFiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                wiFiManager.disableNetwork(i.networkId)
                wiFiManager.removeNetwork(i.networkId)
            }
        }
    }

    fun removeSsid(netwrokId: Int){
        val list = wiFiManager.configuredNetworks
        for (i in list) {
            if (i.networkId == netwrokId) {
                wiFiManager.disableNetwork(i.networkId)
                wiFiManager.removeNetwork(i.networkId)
            }
        }
    }

    fun disableAllNetworks(){
        val list = wiFiManager.configuredNetworks
        for (i in list) {
            wiFiManager.disableNetwork(i.networkId)
        }
    }

    fun enableAllNetworks(){
        val list = wiFiManager.configuredNetworks
        for (i in list) {
            wiFiManager.enableNetwork(i.networkId, false)
        }
    }

    fun addNewNetwork(ssid: String): Int {
        // create new connection
        val conf = WifiConfiguration()
        conf.SSID = "\"" + ssid + "\""   // Please note the quotes. String should contain ssid in quotes
        // For Open network you need to do this:
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        // Then, you need to add it to Android wifi manager settings:
        return wiFiManager.addNetwork(conf)
    }

    fun disconnect(){
        wiFiManager.disconnect()
    }

    fun connectToNetwork(networkId: Int) {
        wiFiManager.disconnect()
        wiFiManager.enableNetwork(networkId, true)
        wiFiManager.reconnect()
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun registerOnConncted(listener: OnWiFiConnectedListener, ssid: String) {

        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                var info = connectivityManager.getNetworkInfo(network)
                if (info != null && info.detailedState == NetworkInfo.DetailedState.CONNECTED
                    &&  "\"" + ssid + "\"" == info.extraInfo)
                {
                    networkCallback = null
                    connectivityManager.unregisterNetworkCallback(this)
                    listener.onConnected()
                }
            }
        }
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    fun unregisterOnConnected(){
        if (networkCallback != null){
            val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback)
            networkCallback = null
        }
    }

    private val wiFiManager: WifiManager
    get (){
        if (mWiFiManager == null)
            mWiFiManager = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return mWiFiManager!!
    }

    private val locationManager: LocationManager
        get (){
            if (mLocationManager == null)
                mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return mLocationManager!!
        }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    interface OnWiFiConnectedListener{
        fun onConnected()
    }
}