package pl.sviete.dom.devices.net.Models

data class AccessPointInfo(val ssid: String, var isAis: Boolean): Comparable<AccessPointInfo>{
    override fun toString(): String {
        return ssid
    }

    override fun compareTo(other: AccessPointInfo): Int {
        val result = isAis.compareTo(other.isAis)
        if (result == 0)
            return ssid.compareTo(other.ssid)
        if (result == 1)
            return -1
        return 1
    }
}