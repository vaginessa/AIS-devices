package pl.sviete.dom.devices.net.Models

class DeviceInfo {
    private var mIP: String? = null
    private var mName: String

    constructor(name: String) {
        mName = name
    }

    constructor(name: String, ip: String) {
        mName = name
        mIP = ip
    }

    override fun toString(): String {
        return "$mName, IP: $mIP"
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is DeviceInfo) obj.mName == this.mName else false
    }
}
