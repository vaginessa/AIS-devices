package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.WiFiScanner
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_creator_aplist_.*
import pl.sviete.dom.devices.net.AccessPointInfo

class AplistCreatorFragment : Fragment() {

    private var mApSelectedListener: OnAPSelectedListener? = null
    private var mWifi: WiFiScanner? = null
    private var mAisAdapter: ArrayAdapter<AccessPointInfo>? = null
    private val mAisList = ArrayList<AccessPointInfo>()
    private var mOthersAdapter: ArrayAdapter<AccessPointInfo>? = null
    private val mOthersList = ArrayList<AccessPointInfo>()

    companion object {
        fun newInstance() = AplistCreatorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creator_aplist_, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val wifi = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        mWifi = WiFiScanner(wifi, lm)

        mAisAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, mAisList)
        ais_ap_list.adapter = mAisAdapter

        mOthersAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, mOthersList)
        others_ap_list.adapter = mOthersAdapter

        swiperefresh.setOnRefreshListener {
            loadData()
            swiperefresh.isRefreshing = false
        }

        ais_ap_list.setOnItemClickListener { parent, view, position, id ->
            val apInfo = parent.getItemAtPosition(position) as AccessPointInfo
            mApSelectedListener?.OnAPSelected(apInfo)
        }

        others_ap_list.setOnItemClickListener { parent, view, position, id ->
            val apInfo = parent.getItemAtPosition(position) as AccessPointInfo
            mApSelectedListener?.OnAPSelected(apInfo)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAPSelectedListener)
            mApSelectedListener = context
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            loadData()
        }
    }

    private fun loadData(){
        mAisList.clear()
        mOthersList.clear()
        mWifi!!.GetAccesibleAccessPoints()?.forEach {
            val masks = resources.getStringArray(R.array.ais_device_masks)
            if ((masks.filter { m -> it.ssid.contains(m, true)}).any())
                mAisList.add(it)
            else
                mOthersList.add(it)
        }
        mAisAdapter!!.notifyDataSetChanged()
        mOthersAdapter!!.notifyDataSetChanged()
    }

    interface OnAPSelectedListener{
        fun OnAPSelected(apInfo: AccessPointInfo)
    }
}
