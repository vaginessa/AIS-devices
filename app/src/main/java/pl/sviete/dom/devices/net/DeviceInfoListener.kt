package pl.sviete.dom.devices.net

import pl.sviete.dom.devices.net.Models.DeviceInfo

interface DeviceInfoListener {
    fun NewDevice(devInfo: DeviceInfo)
    fun LostDevice(devInfo: DeviceInfo)
}
