package pl.sviete.dom.devices.models

import java.io.Serializable

class AisDevice(id: String) : Serializable {
    val mId = id

    var name: String? = null

    override fun toString(): String {
        return "$name, ID:$mId"
    }
}