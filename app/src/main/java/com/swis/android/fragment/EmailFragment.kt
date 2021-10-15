package com.swis.android.fragment


import com.swis.android.R
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.swis.android.adapter.CustomRecyclerEmailAdapter
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentMailBinding
import com.swis.android.model.Contacts
import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.View
import com.swis.android.util.Util


class EmailFragment : BaseFragment(), CustomRecyclerEmailAdapter.EventListener {


    private val isSearched: Boolean? = false
    var binding: FragmentMailBinding? = null
    var contacts = ArrayList<Contacts>()
    private lateinit var adapter: CustomRecyclerEmailAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_mail
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding = views as FragmentMailBinding?

        setUpView()
        attachListeners()

        binding!!.searchView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding!!.imgDelete.visibility = if (s.length > 0) View.VISIBLE else View.GONE

                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {

                // filter your list from your input
                filter(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })

        binding!!.send.setOnClickListener {
            val emailLauncher = Intent(Intent.ACTION_SEND_MULTIPLE)
            emailLauncher.type = "message/rfc822"
            emailLauncher.putExtra(Intent.EXTRA_EMAIL, adapter.getContacts())
            emailLauncher.putExtra(Intent.EXTRA_SUBJECT, "You have been invited to join swis")
            emailLauncher.putExtra(Intent.EXTRA_TEXT, "Please join me on SWIS App to See What I Search. https://swis.app")
            emailLauncher.setPackage("com.google.android.gm");

            try {
                startActivity(emailLauncher)
            } catch (e: ActivityNotFoundException) {
                showToast("Please install Gmail app")

            }

        }
        binding!!.imgDelete.setOnClickListener {
            binding!!.searchView.setText("")
        }




    }

    override fun onEvent(size: Int) {
        if (size == 0)
            binding!!.send.text = "Send (" + 0 + ")"
        else
            binding!!.send.text = "Send (" + size + ")"
    }


    private fun attachListeners() {

        binding!!.tvCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding!!.searchView.setText("")
                Util.hideSoftKeyboard(activity)
                filter("")

            }

        })
    }

    private fun setUpView() {
        adapter = CustomRecyclerEmailAdapter(mContext, getNameEmailDetails(), this)
        binding!!.fastScrollerRecycler.layoutManager = LinearLayoutManager(mContext)
        binding!!.fastScrollerRecycler.adapter = adapter
        binding!!.fastScrollerRecycler.setIndexTextSize(12)
        binding!!.fastScrollerRecycler.setIndexBarColor("#FFFFFF")
        binding!!.fastScrollerRecycler.setIndexBarCornerRadius(0)
        binding!!.fastScrollerRecycler.setIndexBarTransparentValue(1.toFloat())
        binding!!.fastScrollerRecycler.setIndexbarMargin(0f)
        binding!!.fastScrollerRecycler.setIndexbarWidth(40f)
        binding!!.fastScrollerRecycler.setPreviewPadding(1)
        binding!!.fastScrollerRecycler.setIndexBarTextColor("#ff4285f4")

        binding!!.fastScrollerRecycler.setPreviewTextSize(60)
        binding!!.fastScrollerRecycler.setPreviewColor("#33334c")
        binding!!.fastScrollerRecycler.setPreviewTextColor("#FFFFFF")
        binding!!.fastScrollerRecycler.setPreviewTransparentValue(0.6f)

        binding!!.fastScrollerRecycler.setIndexBarVisibility(true)
        binding!!.fastScrollerRecycler.setIndexbarHighLateTextColor("#33334c")
        binding!!.fastScrollerRecycler.setIndexBarHighLateTextVisibility(true)
    }


    private fun filter(text: String) {
        if (text.length > 0) {
            binding!!.tvCancel.visibility = View.VISIBLE
        } else {
            binding!!.tvCancel.visibility = View.GONE

        }

        val temp = java.util.ArrayList<Contacts>()
        for (d in contacts) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d)
            }
        }
        //update recyclerview
        adapter.updateList(temp)

    }

    private fun getNameEmailDetails(): ArrayList<Contacts> {

        val cr = mContext.contentResolver
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.DATA)


        val cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, null, null, null)

        if (cur != null) {


            try {
                while (cur!!.moveToNext()) {
                    //to get the contact names
                    val name = cur!!.getString(cur!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone = cur!!.getString(cur!!.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    if (phone != null) {
                        contacts.add(Contacts(name, phone))
                    }
                }
            } finally {
                cur!!.close()
            }


        }

        return contacts.distinctBy { Pair(it.name, it.number) } as java.util.ArrayList<Contacts>
    }
}
