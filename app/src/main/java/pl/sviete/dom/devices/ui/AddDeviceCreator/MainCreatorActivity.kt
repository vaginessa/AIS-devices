package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.app.Activity
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R

class MainCreatorActivity : AppCompatActivity(), OnNextStepListener {


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

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

    override fun onNext() {
        viewPager.currentItem = 1
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, ac: Activity) : FragmentPagerAdapter(fm) {

        private val mActivity: Activity = ac

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            when (position) {
                0 -> {
                    val start = StartCreatorFragment.newInstance()
                    start.setOnNextStepListener(mActivity as OnNextStepListener)
                    return start
                }
                1 -> return AplistCreatorFragment.newInstance()
                else -> return ApDataCreatorFragment.newInstance()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
