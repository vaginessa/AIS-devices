package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_creator_name.*

import pl.sviete.dom.devices.R

class NameCreatorFragment : Fragment() {

    private var listener: OnNameAcceptListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val apName = arguments!!.getString("APname")
        txt_name_text.setText(getString(R.string.text_name_fragment, apName))
        val defName = arguments!!.getString("defDeviceName")
        if (defName != null)
            txt_device_name.setText(defName)

        btn_accept_name.setOnClickListener{
            listener?.onNameAccept(txt_device_name.text.toString())
        }
        btn_cancel_name.setOnClickListener{
            listener?.onNameCancel(txt_device_name.text.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNameAcceptListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnNameAcceptListener {
        fun onNameAccept(name: String)
        fun onNameCancel(name: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NameCreatorFragment()
    }
}
