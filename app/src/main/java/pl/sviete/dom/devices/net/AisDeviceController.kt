package pl.sviete.dom.devices.net

import android.content.Context
import android.os.Handler
import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.support.v4.os.HandlerCompat.postDelayed




class AisDeviceController(context: Context): WiFiScanner.OnWiFiConnectedListener {
    private val TAG = AisDeviceController::class.java.simpleName

    companion object {
        var lastId: Int = 0
    }

    //private val mContext: Context = context
    private val mWiFiScanner: WiFiScanner = WiFiScanner(context)
    private var mDeviceSsid: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mDeviceNetworkId: Int? = null
    private var mCurrentNetworkId: Int? = null
    private var mListener: OnAddDeviceFinishedListener? = null
    private var mHandlerTimeout = Handler()

    init {
        if (context is OnAddDeviceFinishedListener)
            mListener = context
    }

    fun cancelPair() {
        mWiFiScanner.unregisterOnConnected()
    }

    fun pairNewDevice(ssid: String, apName: String, apPassword: String){
        mDeviceSsid = ssid
        mAPName = apName
        mAPPassword = apPassword

        // save the current connection - to reconnect after the device will be added
        mCurrentNetworkId = mWiFiScanner.getCurrentNetworkId()

        // check if the connection exists and remove it
        mWiFiScanner.removeSsid(ssid)

        // disable all other wifi network for the time of connection
        mWiFiScanner.disableAllNetworks()

        mWiFiScanner.disconnect()

        mWiFiScanner.registerOnConncted(this, ssid)

        // create new connection
        mDeviceNetworkId = mWiFiScanner.addNewNetwork(ssid)
        mWiFiScanner.connectToNetwork(mDeviceNetworkId!!)
        mHandlerTimeout.postDelayed(timeout, 5000)
    }

    private val timeout = object : Runnable {
        override fun run() {
            try {
                mWiFiScanner.unregisterOnConnected()
                mListener?.onAddDeviceFinished(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun OnConnected() {
        mHandlerTimeout.removeCallbacks(timeout)
        try {
            val uuid = "device$lastId"
            val url = URLEncoder.encode("Backlog FriendlyName1 $uuid; SSId1 $mAPName; Password1 $mAPPassword","UTF-8")
            if (connectAndConfiguraDevice(url)) {
                lastId += 1
                mListener?.onAddDeviceFinished(true, uuid)
                return
            }
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "OnConnected", e)
        }
        mListener?.onAddDeviceFinished(false)
    }

    private fun connectAndConfiguraDevice(url: String): Boolean {
        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
        }
        else {
            try {
                val obj = URL("http://192.168.4.1/cm?cmnd=$url")

                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("User-Agent", "Mozilla/5.0")

                if (con.responseCode == HttpURLConnection.HTTP_OK) {
                    mWiFiScanner.removeSsid(mDeviceNetworkId!!)
                    mWiFiScanner.enableAllNetworks()
                    mWiFiScanner.connectToNetwork(mCurrentNetworkId!!)
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, "ConnectAndConfiguraDevice", e)
            }
        }
        return false
    }

    interface  OnAddDeviceFinishedListener {
        fun onAddDeviceFinished(result: Boolean, uuid: String? = null)
    }
}