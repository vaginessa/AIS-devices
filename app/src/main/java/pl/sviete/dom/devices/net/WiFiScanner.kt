package pl.sviete.dom.devices.net

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.util.Log

class WiFiScanner (context: Context) {
    private val mContext = context
    private val TAG = WiFiScanner::class.java.simpleName

    fun LookForAP(): List<AccessPointInfo>?{
        try {
            val manager = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val scans = manager.scanResults
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

    fun EnableWifi(enabled: Boolean) {
        try {
            val manager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            manager.isWifiEnabled = enabled
        } catch (e: Exception) {
            Log.e(TAG, "onReceiveWifiEnable $e")
        }
    }

    fun isLocationEnabled(): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}