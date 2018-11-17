package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.AccessPointInfo
import java.lang.Exception
import android.content.BroadcastReceiver
import android.content.IntentFilter
import pl.sviete.dom.devices.net.AisDeviceController
import pl.sviete.dom.devices.net.WiFiScanner


class MainCreatorActivity : AppCompatActivity(), StartCreatorFragment.OnNextStepListener, AplistCreatorFragment.OnAPSelectedListener
                            , ApDataCreatorFragment.OnAPDataAcceptListener{

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mAPInfo: AccessPointInfo? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mReceiver: WiFiReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_creator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, this)

        // Set up the ViewPager with the sections adapter.
        viewPager.adapter = mSectionsPagerAdapter
    }

    override fun onStartDesigner() {
        viewPager.currentItem = 1
    }

    override fun OnAPSelected(apInfo : AccessPointInfo){
        mAPInfo = apInfo
        viewPager.currentItem = 2
    }

    override fun OnAPDataCancel() {
        mAPInfo = null
        viewPager.currentItem = 1
    }

    override fun OnAPDataAccept(name: String, password: String) {
        mAPName = name
        mAPPassword = password

//        val filter = IntentFilter()
//        filter.addAction("android.net.wifi.STATE_CHANGE")
//        mReceiver = WiFiReceiver()
//        registerReceiver(mReceiver, filter)
        val wifi = WiFiScanner(this)
        val aisCtrl = AisDeviceController(wifi)
        aisCtrl.PairNewDevice(mAPInfo!!.ssid, name, password)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (mReceiver != null)
//            unregisterReceiver(mReceiver)
//    }

    inner class SectionsPagerAdapter(fm: FragmentManager, ac: Activity) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return StartCreatorFragment.newInstance()
                1 -> return AplistCreatorFragment.newInstance()
                2 -> return ApDataCreatorFragment.newInstance()
                else -> throw Exception("Not implemented")
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
