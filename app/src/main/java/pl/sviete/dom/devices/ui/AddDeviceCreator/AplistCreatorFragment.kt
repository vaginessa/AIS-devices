package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.WiFiScanner
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.aplist_creator_fragment.*
import pl.sviete.dom.devices.net.AccessPointInfo

class AplistCreatorFragment : Fragment() {

    private var mWifi: WiFiScanner? = null
    private var aisAdapter: ArrayAdapter<AccessPointInfo>? = null
    private val listItems = ArrayList<AccessPointInfo>()

    companion object {
        fun newInstance() = AplistCreatorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.aplist_creator_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mWifi = WiFiScanner(activity!!.applicationContext)

        aisAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, listItems)
        ais_ap_list.adapter = aisAdapter

        swiperefresh.setOnRefreshListener {
            loadData()
            swiperefresh.isRefreshing = false
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            loadData()
        }
    }

    private fun loadData(){
        listItems.clear()
        val aps = mWifi!!.LookForAP()
        if (aps != null)
            listItems.addAll(aps)
        aisAdapter!!.notifyDataSetChanged()
    }
}
