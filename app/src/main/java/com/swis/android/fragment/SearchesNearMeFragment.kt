package com.swis.android.fragment


import androidx.databinding.ViewDataBinding

import com.swis.android.R
import com.swis.android.activity.HomeActivity
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentSearchesNearMeBinding


class SearchesNearMeFragment : BaseFragment() {

    var binding: FragmentSearchesNearMeBinding?=null


    override fun getLayoutId(): Int {
        return R.layout.fragment_searches_near_me
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding= views as FragmentSearchesNearMeBinding?
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setTitle("Searches near me")
        (activity as HomeActivity).actionBarSetup()

    }


}
