package pl.sviete.dom.devices.net

import android.net.wifi.WifiConfiguration



class AisDeviceController(wifiScanner: WiFiScanner) {
    private val mWiFiScanner = wifiScanner

    fun PairNewDevice(ssid: String, apName: String, apPassword: String){
        // save the current connection - to reconnect after the device will be added
        val currentNetworkId = mWiFiScanner.getCurrentNetworkId()

        // check if the connection exists and remove it
        mWiFiScanner.removeSsid(ssid)

        // disable all other wifi network for the time of connection
        mWiFiScanner.disableAllNetworks()

        // create new connection
        val newNetworkId = mWiFiScanner.addNewNetwork(ssid)
        mWiFiScanner.connectToNetwork(newNetworkId)
    }
}