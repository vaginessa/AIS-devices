package pl.sviete.dom.devices.ui.adddevicecreator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_creator_start.*
import pl.sviete.dom.devices.R
import android.content.Context


/**
 * A placeholder fragment containing a simple view.
 */
class StartCreatorFragment : Fragment() {
    private var mNextStepCallback: OnNextStepListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_start, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        start_designer_button.setOnClickListener {
            mNextStepCallback?.onStartDesigner()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNextStepListener) {
            mNextStepCallback = context
        }
    }

    interface OnNextStepListener {
        fun onStartDesigner()
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
        fun newInstance(): StartCreatorFragment {
            return StartCreatorFragment()
        }
    }
}