package pl.sviete.dom.devices.ui.AddDeviceCreator

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import kotlinx.android.synthetic.main.item_ap_list.view.*
import pl.sviete.dom.devices.net.Models.AccessPointInfo
import android.support.v4.app.Fragment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan

class APAdapter (val items : ArrayList<AccessPointInfo>, val context: Context, val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ap_list, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    interface OnItemClickListener {
        fun onItemClick(item: AccessPointInfo)
    }
}

class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
    val apName = view.txt_ap_name
    val masks = view.resources.getStringArray(R.array.ais_device_masks)

    fun bind(item: AccessPointInfo , listener: APAdapter.OnItemClickListener) {

        if ((masks.filter { m -> item.ssid.contains(m, true)}).any()) {
            apName.text = SpannableStringBuilder(item.ssid).apply{
                setSpan(StyleSpan(Typeface.BOLD), 0, item.ssid.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        else
            apName.text = item.ssid
        itemView.setOnClickListener{
            listener.onItemClick(item)
        }
    }
}
