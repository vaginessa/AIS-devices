package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.AccessPointInfo
import java.lang.Exception
import pl.sviete.dom.devices.net.AisDeviceController

class MainCreatorActivity : AppCompatActivity(), StartCreatorFragment.OnNextStepListener, AplistCreatorFragment.OnAPSelectedListener
                            , ApDataCreatorFragment.OnAPDataAcceptListener, NameCreatorFragment.OnNameAcceptListener
                            , AisDeviceController.OnAddDeviceFinishedListener{

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mAPInfo: AccessPointInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_creator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

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
        progressBar.visibility = View.VISIBLE

        val aisCtrl = AisDeviceController(this)
        aisCtrl.pairNewDevice(mAPInfo!!.ssid, name, password)
    }

    override fun onAddDeviceFinished(result: Boolean, uuid: String?) {
        runOnUiThread {
            progressBar.visibility = View.GONE
            if (result)
                viewPager.currentItem = 3
        }
    }

    override fun onNameAccept(name: String) {
        finish()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return StartCreatorFragment.newInstance()
                1 -> return AplistCreatorFragment.newInstance()
                2 -> return ApDataCreatorFragment.newInstance()
                3 -> return NameCreatorFragment.newInstance()
                else -> throw Exception("Not implemented")
            }
        }

        override fun getCount(): Int {
            return 4
        }
    }
}
