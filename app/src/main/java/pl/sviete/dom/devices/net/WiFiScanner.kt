package pl.sviete.dom.devices.net

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.net.wifi.WifiInfo



class WiFiScanner (wifiManager: WifiManager, locationManager: LocationManager) {
    private val mWifiManager = wifiManager
    private val mLocationManager = locationManager
    private val TAG = WiFiScanner::class.java.simpleName

    fun GetAccesibleAccessPoints(): List<AccessPointInfo>?{
        try {
            val scans = mWifiManager.scanResults
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
            mWifiManager.isWifiEnabled = enabled
        } catch (e: Exception) {
            Log.e(TAG, "onReceiveWifiEnable $e")
        }
    }

    fun getCurrentNetworkId(): Int{
        val info = mWifiManager.connectionInfo
        return info.networkId
    }

    fun removeSsid(ssid: String){
        val list = mWifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                mWifiManager.disableNetwork(i.networkId)
                mWifiManager.removeNetwork(i.networkId)
                mWifiManager.saveConfiguration()
            }
        }
    }

    fun disableAllNetworks(){
        val list = mWifiManager.configuredNetworks
        for (i in list) {
            mWifiManager.disableNetwork(i.networkId)
        }
    }

    fun addNewNetwork(ssid: String): Int {
        // create new connection
        val conf = WifiConfiguration()
        conf.SSID = "\"" + ssid + "\""   // Please note the quotes. String should contain ssid in quotes
        // For Open network you need to do this:
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        // Then, you need to add it to Android wifi manager settings:
        return mWifiManager.addNetwork(conf)
    }

    fun connectToNetwork(networkId: Int) {
        mWifiManager.disconnect()
        mWifiManager.enableNetwork(networkId, true)
        mWifiManager.reconnect()
    }

    private fun isLocationEnabled(): Boolean {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}