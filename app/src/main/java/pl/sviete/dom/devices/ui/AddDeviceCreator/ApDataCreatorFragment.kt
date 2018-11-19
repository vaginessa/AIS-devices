package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_creator_ap_data.*

import pl.sviete.dom.devices.R

class ApDataCreatorFragment : Fragment() {

    private var mAPDataAcceptListener: OnAPDataAcceptListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_ap_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_cancel.setOnClickListener{
            mAPDataAcceptListener?.onAPDataCancel()
        }

        btn_accept.setOnClickListener{
            btn_accept.isEnabled = false
            mAPDataAcceptListener?.onAPDataAccept(txt_ap_name.text.toString(), txt_ap_password.text.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAPDataAcceptListener) {
            mAPDataAcceptListener = context
        }
    }

    fun activateForm() {
        btn_accept.isEnabled = true
    }

    companion object {

        @JvmStatic
        fun newInstance() = ApDataCreatorFragment()
    }

    interface OnAPDataAcceptListener{
        fun onAPDataCancel()
        fun onAPDataAccept(name: String, password: String)
    }
}
