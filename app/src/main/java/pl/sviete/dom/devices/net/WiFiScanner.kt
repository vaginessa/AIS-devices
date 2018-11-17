package pl.sviete.dom.devices.net

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.content.Intent
import android.net.*
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Build


class WiFiScanner (context: Context) {
    private val mContext = context
    private val TAG = WiFiScanner::class.java.simpleName


    fun GetAccesibleAccessPoints(): List<AccessPointInfo>?{
        try {
            val scans = wiFiManager!!.scanResults
            if (scans == null) {
                //  "Failed getting scan results"
            } else if (scans.isEmpty() && !isLocationEnabled()) {
                // https://issuetracker.google.com/issues/37060483:
                // "WifiManager#getScanResults() returns an empty array list if GPS is turned off"
                //  "Location needs to be enabled on the device"
            } else {
                val result = mutableListOf<AccessPointInfo>()
                for (scan in scans) {
                    val ap = AccessPointInfo(scan.SSID)
                    result.add(ap)
                }
                return result
            }
        } catch (e: Exception) {
            Log.e(TAG, "onReceiveWifiScanInfo $e")
        }
        return null
    }

    fun enableWiFi(enabled: Boolean) {
        try {
            wiFiManager!!.isWifiEnabled = enabled
        } catch (e: Exception) {
            Log.e(TAG, "onReceiveWifiEnable $e")
        }
    }

    fun getCurrentNetworkId(): Int{
        val info = wiFiManager!!.connectionInfo
        return info.networkId
    }

    fun removeSsid(ssid: String){
        val list = wiFiManager!!.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                wiFiManager!!.disableNetwork(i.networkId)
                wiFiManager!!.removeNetwork(i.networkId)
            }
        }
    }

    fun removeSsid(netwrokId: Int){
        val list = wiFiManager!!.configuredNetworks
        for (i in list) {
            if (i.networkId == netwrokId) {
                wiFiManager!!.disableNetwork(i.networkId)
                wiFiManager!!.removeNetwork(i.networkId)
            }
        }
    }

    fun disableAllNetworks(){
        val list = wiFiManager!!.configuredNetworks
        for (i in list) {
            wiFiManager!!.disableNetwork(i.networkId)
        }
    }

    fun enableAllNetworks(){
        val list = wiFiManager!!.configuredNetworks
        for (i in list) {
            wiFiManager!!.enableNetwork(i.networkId, false)
        }
    }

    fun addNewNetwork(ssid: String): Int {
        // create new connection
        val conf = WifiConfiguration()
        conf.SSID = "\"" + ssid + "\""   // Please note the quotes. String should contain ssid in quotes
        // For Open network you need to do this:
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        // Then, you need to add it to Android wifi manager settings:
        return wiFiManager!!.addNetwork(conf)
    }

    fun disconnect(){
        wiFiManager!!.disconnect()
    }

    fun connectToNetwork(networkId: Int) {
        wiFiManager!!.disconnect()
        wiFiManager!!.enableNetwork(networkId, true)
        wiFiManager!!.reconnect()
    }

    fun registerOnConncted(listener: OnWiFiConnectedListener, ssid: String) {

        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

        connectivityManager!!.registerNetworkCallback(builder.build(), object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                var info = connectivityManager.getNetworkInfo(network)
                if (info.detailedState == NetworkInfo.DetailedState.CONNECTED
                    &&  "\"" + ssid + "\"" == info.extraInfo)
                {
                    listener.OnConnected()
                }
            }
        })
    }

    private val wiFiManager: WifiManager? = null
    get (){
        if (field == null)
            field = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return field
    }

    private val locationManager: LocationManager? = null
        get (){
            if (field == null)
                field = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return field
        }

    private fun isLocationEnabled(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    interface OnWiFiConnectedListener{
        fun OnConnected()
    }
}