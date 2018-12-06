package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
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


    private var mAPInfo: AccessPointInfo? = null
    private var mAccessibleAP: List<AccessPointInfo>? = null
    private val mIntentResult = Intent()
    private val mAisCtrl = AisDeviceController(this)
    private var mCurrentFragment: Fragment? = null
    private var mNewDeviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_creator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return
        }

        val firstFragment = getFragment(0)!!
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, firstFragment)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        mAisCtrl.cancelPair()
        progressBar.visibility = View.GONE
    }

    override fun onStartDesigner() {
        changeFragment(1)
    }

    override fun onAPSelected(apInfo : AccessPointInfo, accessibleAP: List<AccessPointInfo>){
        mAPInfo = apInfo
        mAccessibleAP = accessibleAP
        changeFragment(2, true)
    }

    override fun onNameCancel(name: String) {
        mNewDeviceName = name
        supportFragmentManager.popBackStack()
    }

    override fun onNameAccept(name: String) {
        mNewDeviceName = name
        changeFragment(3, true)
    }

    override fun onAPDataCancel() {
        mAisCtrl.cancelPair()
        progressBar.visibility = View.GONE
        supportFragmentManager.popBackStack()
    }

    override fun onAPDataAccept(name: String, password: String) {
        progressBar.visibility = View.VISIBLE
        mAisCtrl.pairNewDevice(mAPInfo!!.ssid, name, password, mNewDeviceName!!)
    }

    override fun onAddDeviceFinished(result: Boolean) {
        if (result) {
            val ais = AisDevice(mAPInfo!!.mac)
            mIntentResult.putExtra("aisdevice", ais)
            mIntentResult.putExtra("name", mNewDeviceName)
            setResult(CREATOR_REQUEST_CODE, mIntentResult)
        }
        else{
            runOnUiThread {
                val apFragment = mCurrentFragment as ApDataCreatorFragment?
                apFragment?.activateForm()
                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show()
            }
        }
        runOnUiThread {
            progressBar.visibility = View.GONE
            if (result)
                finish()
        }
    }

    companion object {
        const val CREATOR_REQUEST_CODE = 111
    }

    private fun changeFragment(position: Int, canBack: Boolean = false) {
        val newFragment = getFragment(position)
        if (newFragment == null) return
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.exit_to_left, R.anim.slide_in_left, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, newFragment)
        if (canBack)
            transaction.addToBackStack(null)
        transaction.commit()
        mCurrentFragment = newFragment
    }

    private fun getFragment(position: Int): Fragment? {
        when (position) {
            0 -> return StartCreatorFragment.newInstance()
            1 -> return AplistCreatorFragment.newInstance()
            2 -> {
                val bundle = Bundle()
                bundle.putString("APname", mAPInfo?.ssid)
                bundle.putString("defDeviceName", mNewDeviceName)
                val nameFragment = NameCreatorFragment.newInstance()
                nameFragment.arguments = bundle
                return nameFragment
            }
            3 ->{
                val aps = mAccessibleAP!!.filter { x -> x != mAPInfo }.map { x -> x.ssid }.toTypedArray()
                if (aps.isEmpty()) return null
                val bundle = Bundle()
                bundle.putStringArray("accessibleAP", aps)
                val fragment = ApDataCreatorFragment.newInstance()
                fragment.arguments = bundle
                return fragment
            }
        }
        throw Exception("Not implemented")
    }
}
