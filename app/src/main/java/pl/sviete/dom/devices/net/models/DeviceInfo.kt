package pl.sviete.dom.devices.net.models

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

    override fun equals(other: Any?): Boolean {
        return if (other is DeviceInfo) other.mName == this.mName else false
    }
}
