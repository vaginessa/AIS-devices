package pl.sviete.dom.devices.net

interface DeviceInfoListener {
    fun NewDevice(devInfo: DeviceInfo)
    fun LostDevice(devInfo: DeviceInfo)
}
