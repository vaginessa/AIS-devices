package pl.sviete.dom.devices.net

data class AccessPointInfo(val ssid: String){
    override fun toString(): String {
        return ssid
    }
}