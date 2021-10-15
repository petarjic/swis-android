package com.swis.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SectionIndexer
import android.widget.TextView
import com.swis.android.R
import com.swis.android.model.Contacts
import java.util.*
import android.content.Intent
import android.net.Uri
import com.swis.android.custom.customviews.CircularTextView

class CustomRecyclerSmsAdapter(context: Context?, list: ArrayList<Contacts>) : androidx.recyclerview.widget.RecyclerView.Adapter<CustomRecyclerSmsAdapter.MyEmailViewHolder>(), SectionIndexer {
    var mDataArray: List<Contacts>? = list.sortedWith(compareBy({ it.name }))
    private var mSectionPositions: ArrayList<Int>? = null
    var mContext = context
    lateinit var section: String
    var color = arrayOf<String>("#FF007F","#7F00FF","#FF00FF","#FFE4B5","#D2691E","#B22222","#9932CC","#98FB98","#9400D3","#9370DB")


    override fun getSections(): Array<String> {
        var sections = ArrayList<String>(26)
        mSectionPositions = ArrayList<Int>(26)
        var i = 0
        val size = mDataArray!!.size
        while (i < size) {
            section = mDataArray!![i].name[0].toUpperCase().toString()
            if (!sections.contains(section)) {
                sections.add(section)
                mSectionPositions!!.add(i)
            }

            i++
        }
        return sections.toTypedArray()
    }

    override fun getSectionForPosition(p0: Int): Int {
        return 0
    }

    override fun getPositionForSection(p0: Int): Int {

        return mSectionPositions!!.get(p0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEmailViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false)
        return MyEmailViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mDataArray!!.size
    }

    override fun onBindViewHolder(holder: MyEmailViewHolder, position: Int) {
        val name = mDataArray!!.get(position).name
        var clientName = name.get(0).toString()
        for (i in 1 until name.length) {
            if (name.get(i) == ' ' && name.length > i + 1) {
                clientName += name.get(i + 1)
                break
            }
        }

        val rnd = Random()

        if (position == 0) {
            holder?.tv_label.text = mDataArray!!.get(position).name[0].toString()

            holder?.ctv_label.setStrokeWidth(1)
            holder?.ctv_label.text=clientName
            holder?.ctv_label.setStrokeColor("#FFFFFF")
            holder?.ctv_label.setSolidColor(color[rnd.nextInt(color.size)])
            holder?.tv_label.visibility = View.VISIBLE

        } else if (!mDataArray!!.get(position - 1).name[0].toString().equals(mDataArray!!.get(position).name[0].toString(), true)) {
            holder?.tv_label.text = mDataArray!!.get(position).name[0].toString()
            holder?.ctv_label.text=clientName
            holder?.ctv_label.setStrokeWidth(1)
            holder?.ctv_label.setStrokeColor("#FFFFFF")
            holder?.ctv_label.setSolidColor(color[rnd.nextInt(color.size)])
            holder?.tv_label.visibility = View.VISIBLE
        } else {
            holder?.tv_label.visibility = View.GONE
            holder?.ctv_label.text=clientName
            holder?.ctv_label.setStrokeWidth(1)
            holder?.ctv_label.setStrokeColor("#FFFFFF")
            holder?.ctv_label.setSolidColor(color[rnd.nextInt(color.size)])

        }


        holder?.tv_name.text = mDataArray!!.get(position).name
        holder?.tv_phone.text = mDataArray!!.get(position).number
        holder?.share.setOnClickListener {
            val uri = Uri.parse("smsto:" + mDataArray!![position].number)
            val share = Intent(Intent.ACTION_SENDTO, uri)
            share.putExtra("sms_body", "Please join me on SWIS App to See What I Search. https://swis.app")
            mContext!!.startActivity(share)
        }
    }

    fun sendSMS(v: View) {

    }

    class MyEmailViewHolder(itemView: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView!!) {
        var tv_name = itemView!!.findViewById<TextView>(R.id.tvContactName)
        var tv_phone = itemView!!.findViewById<TextView>(R.id.tvPhoneNumber)
        var tv_label = itemView!!.findViewById<TextView>(R.id.tvLabel)
        var share = itemView!!.findViewById<ImageView>(R.id.iv_share)
        var ctv_label = itemView!!.findViewById<CircularTextView>(R.id.ctv_label)


    }

    fun updateList(list: List<Contacts>) {
        mDataArray = list.sortedWith(compareBy({ it.name }))
        notifyDataSetChanged()
    }

}

