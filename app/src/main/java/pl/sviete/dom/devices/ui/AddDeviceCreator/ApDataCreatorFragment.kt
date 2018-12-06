package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_creator_ap_data.*

import pl.sviete.dom.devices.R

class ApDataCreatorFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var mAPDataAcceptListener: OnAPDataAcceptListener? = null
    private var mAPName: String? = null
    private var mPassword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_ap_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pref = activity?.getPreferences(Context.MODE_PRIVATE)!!
        mAPName = pref.getString("ap_name", null)
        mPassword = pref.getString("password", null)

        val aps = arguments!!.getStringArray("accessibleAP")
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, aps)
        spinner_ap.adapter = adapter
        spinner_ap.onItemSelectedListener = this

        if (mAPName != null) {
            var idx = adapter.getPosition(mAPName)
            if (idx < 0)
                idx = 0
            spinner_ap.setSelection(idx)
        }
        else {
            spinner_ap.setSelection(0)
        }

        btn_cancel.setOnClickListener{
            mAPDataAcceptListener?.onAPDataCancel()
        }

        btn_accept.setOnClickListener{
            val name = spinner_ap.selectedItem.toString()
            val password = txt_ap_password.text.toString()
            if (chkSavePassword.isChecked)
                savePassword(name, password)

            btn_accept.isEnabled = false
            mAPDataAcceptListener?.onAPDataAccept(name, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAPDataAcceptListener) {
            mAPDataAcceptListener = context
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        txt_ap_password.setText("")
        if (mAPName == spinner_ap.selectedItem.toString())
        {
            txt_ap_password.setText(mPassword)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun savePassword(apName: String, password: String){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("ap_name", apName)
            putString("password", password)
            commit()
        }
        mAPName = apName
        mPassword = password
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
