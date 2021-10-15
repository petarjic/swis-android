package com.swis.android.fragment


import android.content.Intent
import android.net.Uri
import android.view.View

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide

import com.swis.android.R
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentShareBinding
import com.swis.android.util.Util
import com.squareup.picasso.Picasso


class ShareFragment : BaseFragment() {

    var binding: FragmentShareBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_share
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding = views as FragmentShareBinding?
        var user = prefHelper.userInfo

        if (user.name != null)
            binding!!.tvName.text = user.name
        if (user.username != null)
            binding!!.tvUsername.text = "@" + user.username
        if (user.bio != null)
            binding!!.tvBio.setText(user.bio.toString())
        if (user.avatar != null)
            Glide.with(mContext).load(Uri.parse(user.avatar)).placeholder(R.drawable.default_user
            ).error(R.drawable.intro1).into(binding!!.profilePic)



        binding!!.btnShare.setOnClickListener {

            var share = Intent(Intent.ACTION_SEND)
            share.setType("image/*")
            share.putExtra(Intent.EXTRA_STREAM, Util.getImageUri(mContext, Util.getBitmapFromView(mContext, binding!!.llProfile as View?)))
            startActivity(Intent.createChooser(share, "My_profile"))

        }
    }


}
