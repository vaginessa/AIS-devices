package pl.sviete.dom.devices.net

import pl.sviete.dom.devices.net.models.DeviceInfo

interface DeviceInfoListener {
    fun NewDevice(devInfo: DeviceInfo)
    fun LostDevice(devInfo: DeviceInfo)
}
