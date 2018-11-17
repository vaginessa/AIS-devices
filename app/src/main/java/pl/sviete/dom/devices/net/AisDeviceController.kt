package pl.sviete.dom.devices.net

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class AisDeviceController(wifiScanner: WiFiScanner): WiFiScanner.OnWiFiConnectedListener {
    private val TAG = AisDeviceController::class.java.simpleName
    private val mWiFiScanner = wifiScanner
    private var mDeviceSsid: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mDeviceNetworkId: Int? = null
    private var mCurrentNetworkId: Int? = null

    fun PairNewDevice(ssid: String, apName: String, apPassword: String){
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
    }

    override fun OnConnected() {
        // step 1 call iot Using backlog
        var wsRet = ""
        try {
            wsRet = cmnd(
                URLEncoder.encode(
                    "Backlog FriendlyName1 MyName; SSId1 $mAPName; Password1 $mAPPassword",
                    "UTF-8"
                ), ""
            )
        } catch (e: UnsupportedEncodingException) {
            return //"Nazwa urzÄdzenia, problem z kodowaniem."
        }

        // validation
        if (wsRet == "ok") {
            return// "ok"
        }
        //Log.d(TAG, "wsRet LOOP:$IotConnectionNumOfTry")
        return //"Nie udaĹo siÄ przesĹaÄ ustawieĹ do urzÄdzenia, sprĂłbuj ponownie."
    }

    fun cmnd(url: String, attrToCheck: String): String {

        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
            return ""
        }

        //Log.d(TAG, "wsRet url: $url")
        try {
            val obj = URL("http://192.168.4.1/cm?cmnd=$url")

            val con = obj.openConnection() as HttpURLConnection
            con.setRequestMethod("GET")
            con.setRequestProperty("User-Agent", "Mozilla/5.0")

            /*val inp = BufferedReader(
                InputStreamReader(con.getInputStream())
            )
            var inputLine = ""
            val response = StringBuffer()
            while ((inputLine = inp.readLine()) != null) {
                response.append(inputLine)
            }
            inp.close()*/
            return if (con.responseCode == HttpURLConnection.HTTP_OK) {
                onPostExecute()
                //                            Log.d(TAG, "response: " + response.toString());
                //                            JSONObject jResp = new JSONObject(response.toString());
                //                            Log.d(TAG, attrToCheck + ": " + jResp.getString(attrToCheck));
                "ok"
            } else "nok"


        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            return ""
        }
    }

    fun onPostExecute() {
        //Log.d(TAG, IotNameToSettings + " END !!!")
        //Log.d(TAG, IotNameToSettings + " result: " + result)
        //Log.d(TAG, "IotConnectionNumOfTry: $IotConnectionNumOfTry")
        mWiFiScanner.removeSsid(mDeviceNetworkId!!)
        mWiFiScanner.enableAllNetworks()
        mWiFiScanner.connectToNetwork(mCurrentNetworkId!!)
    }
}