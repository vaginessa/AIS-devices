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
    private val mIntentResult = Intent()
    private val mAisCtrl = AisDeviceController(this)
    private var mCurrentFragment: Fragment? = null

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

        // Create a new Fragment to be placed in the activity layout
        val firstFragment = getFragment(0)
        // Add the fragment to the 'fragment_container' FrameLayout
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, firstFragment).commit()
    }

    override fun onPause() {
        super.onPause()
        mAisCtrl.cancelPair()
        progressBar.visibility = View.GONE
    }

    override fun onStartDesigner() {
        changeFragment(1)
    }

    override fun OnAPSelected(apInfo : AccessPointInfo){
        mAPInfo = apInfo
        changeFragment(2, true)
    }

    override fun onAPDataCancel() {
        mAisCtrl.cancelPair()
        mAPInfo = null
        progressBar.visibility = View.GONE
        supportFragmentManager.popBackStack()
    }

    override fun onAPDataAccept(name: String, password: String) {
        progressBar.visibility = View.VISIBLE
        mAisCtrl.pairNewDevice(mAPInfo!!.ssid, name, password)
    }

    override fun onAddDeviceFinished(result: Boolean, uuid: String?) {
        if (result) {
            val ais = AisDevice(uuid!!)
            mIntentResult.putExtra("aisdevice", ais)
            setResult(CREATOR_REQUEST_CODE, mIntentResult)
        }
        else{
            val apFragment = mCurrentFragment as ApDataCreatorFragment
            if (apFragment != null)
                apFragment.activateForm()
            Toast.makeText(this, "Niestety coś poszło nie tak", Toast.LENGTH_LONG).show()
        }
        runOnUiThread {
            progressBar.visibility = View.GONE
            if (result)
                changeFragment(3)
        }
    }

    override fun onNameAccept(name: String) {
        mIntentResult.putExtra("name", name)
        setResult(CREATOR_REQUEST_CODE, mIntentResult)
        finish()
    }

    companion object {
        const val CREATOR_REQUEST_CODE = 111
    }

    fun changeFragment(position: Int, canBack: Boolean = false) {
        val newFragment = getFragment(position)
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.exit_to_left, R.anim.slide_in_left, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, newFragment)
        if (canBack)
            transaction.addToBackStack(null)
        transaction.commit()
        mCurrentFragment = newFragment
    }

     fun getFragment(position: Int): Fragment {
        when (position) {
            0 -> return StartCreatorFragment.newInstance()
            1 -> return AplistCreatorFragment.newInstance()
            2 -> return ApDataCreatorFragment.newInstance()
            3 -> return NameCreatorFragment.newInstance()
            else -> throw Exception("Not implemented")
        }
    }
}
