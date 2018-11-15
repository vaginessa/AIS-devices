package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo



class WiFiReceiver : BroadcastReceiver() {

    private var message = ""

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.net.wifi.STATE_CHANGE")) {

            // wifi state change
            val netInfo = intent.extras.get("networkInfo") as NetworkInfo

            val ssid = netInfo.extraInfo
            val type = netInfo.typeName
            var state = netInfo.state.toString()

            if (state == "CONNECTING") {
                return
            } else if (state == "DISCONNECTING") {
                return
            } else if (state == "SUSPENDED") {
                return
            } else if (state == "UNKNOWN") {
                return
            }

            if (type == "WIFI" && ssid != "<unknown ssid>") {
                // do not repeat the message to user
                if (!message.equals(ssid + " " + netInfo.isConnected)) {
                    message = ssid + " " + netInfo.isConnected

                    // special case connect the iot device
//                    if (("\"" + IotDeviceSsidToConnect + "\"").equals(ssid) && netInfo.isConnected) {
//                        // step 1 wait to be sure the device is connected
//                        // problem with java.net.SocketException: Network is unreachable
//                        Log.d(TAG, "TimeUnit.SECONDS.sleep 6")
//                        try {
//                            TimeUnit.SECONDS.sleep(6)
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//
//                        // 1. add the FriendlyName <name> - Set friendly name (32 chars max)
//                        val task = SetIotSettingsTask()
//                        task.execute(arrayOf<String>())
//                    }
//
//
//
//                    try {
//                        onReceiveWifiConnectionInfo(context)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }

                }
            }
        }
    }
}
