package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_creator_start.*
import pl.sviete.dom.devices.R
import android.app.Activity



/**
 * A placeholder fragment containing a simple view.
 */
class StartCreatorFragment : Fragment() {
    private var mNextStepCallback: OnNextStepListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_creator_start, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        start_designer_button.setOnClickListener {
            mNextStepCallback?.onNext()
        }
    }

    fun setOnNextStepListener(activity: OnNextStepListener) {
        mNextStepCallback = activity
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): StartCreatorFragment {
            val fragment = StartCreatorFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}