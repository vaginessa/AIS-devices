package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.WiFiScanner
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_creator_aplist_.*
import pl.sviete.dom.devices.net.Models.AccessPointInfo
import android.widget.Toast
import java.util.*


class AplistCreatorFragment : Fragment() {

    private var mApSelectedListener: OnAPSelectedListener? = null
    private var mWifi: WiFiScanner? = null
    private var mAisAdapter: APAdapter? = null
    private val mAisList = ArrayList<AccessPointInfo>()

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

        mWifi = WiFiScanner(context!!)

        rv_ap_list.layoutManager = LinearLayoutManager(context)
        mAisAdapter = APAdapter(mAisList, context!!, object : APAdapter.OnItemClickListener {
            override fun onItemClick(item: AccessPointInfo) {
                mApSelectedListener?.OnAPSelected(item)
            }
        })
        rv_ap_list.adapter = mAisAdapter

        swiperefresh.setOnRefreshListener {
            loadData()
            swiperefresh.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAPSelectedListener)
            mApSelectedListener = context
    }

    private fun loadData(){
        mAisList.clear()

        val list = mWifi!!.GetAccesibleAccessPoints()
        if (list != null){

            val masks = resources.getStringArray(R.array.ais_device_masks)
            list.forEach {
                if ((masks.filter { m -> it.ssid.contains(m, true)}).any()) {
                    it.isAis = true
                }
                mAisList.add(it)
            }
            Collections.sort(mAisList)
        }

        mAisAdapter!!.notifyDataSetChanged()
    }

    interface OnAPSelectedListener{
        fun OnAPSelected(apInfo: AccessPointInfo)
    }
}
