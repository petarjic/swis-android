package com.swis.android.fragment


import androidx.databinding.ViewDataBinding

import com.swis.android.R
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentMyProfileBinding

class MyProfileFragment : BaseFragment() {

    var binding: FragmentMyProfileBinding?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_profile
    }

    override fun onViewBinded(views: ViewDataBinding?) {

        binding= views as FragmentMyProfileBinding?

    }




}
