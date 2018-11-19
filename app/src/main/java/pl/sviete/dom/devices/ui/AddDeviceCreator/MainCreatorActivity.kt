package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.Models.AccessPointInfo
import java.lang.Exception
import pl.sviete.dom.devices.net.AisDeviceController
import android.content.Intent
import android.widget.Toast
import pl.sviete.dom.devices.Models.AisDevice


class MainCreatorActivity : AppCompatActivity(), StartCreatorFragment.OnNextStepListener, AplistCreatorFragment.OnAPSelectedListener
                            , ApDataCreatorFragment.OnAPDataAcceptListener, NameCreatorFragment.OnNameAcceptListener
                            , AisDeviceController.OnAddDeviceFinishedListener{

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mAPInfo: AccessPointInfo? = null
    private val mIntentResult = Intent()
    private val mAisCtrl = AisDeviceController(this)
    private var mAPDataFragment: ApDataCreatorFragment? = null

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

    override fun onPause() {
        super.onPause()
        mAisCtrl.cancelPair()
        progressBar.visibility = View.GONE
    }

    override fun onStartDesigner() {
        viewPager.currentItem = 1
    }

    override fun OnAPSelected(apInfo : AccessPointInfo){
        mAPInfo = apInfo
        viewPager.currentItem = 2
    }

    override fun onAPDataCancel() {
        mAisCtrl.cancelPair()
        mAPInfo = null
        progressBar.visibility = View.GONE
        viewPager.currentItem = 1
    }

    override fun onAPDataAccept(name: String, password: String) {
        progressBar.visibility = View.VISIBLE
        mAisCtrl.pairNewDevice(mAPInfo!!.ssid, name, password)
    }

    override fun onAddDeviceFinished(result: Boolean, uuid: String?) {
        if (result) {
            val ais = AisDevice(uuid!!)
            mIntentResult.putExtra("aisdevice", ais)
            setResult(4, mIntentResult)
        }
        else{
            mAPDataFragment!!.activateForm()
            Toast.makeText(this, "Niestety coś poszło nie tak", Toast.LENGTH_LONG).show()
        }
        runOnUiThread {
            progressBar.visibility = View.GONE
            if (result)
                viewPager.currentItem = 3
        }
    }

    override fun onNameAccept(name: String) {
        mIntentResult.putExtra("name", name)
        setResult(4, mIntentResult)
        finish()
    }

    companion object {
        const val CREATOR_REQUEST_CODE = 111
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return StartCreatorFragment.newInstance()
                1 -> return AplistCreatorFragment.newInstance()
                2 -> {
                    mAPDataFragment = ApDataCreatorFragment.newInstance()
                    return mAPDataFragment!!
                 }
                3 -> return NameCreatorFragment.newInstance()
                else -> throw Exception("Not implemented")
            }
        }

        override fun getCount(): Int {
            return 4
        }
    }
}
