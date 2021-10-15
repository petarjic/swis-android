package com.swis.android.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.swis.android.R
import com.swis.android.base.MiddleBaseActivity
import com.swis.android.databinding.ActivityInviteFriendBinding
import com.swis.android.databinding.AppActionbarBinding
import com.swis.android.fragment.EmailFragment
import com.swis.android.fragment.ShareFragment
import com.swis.android.fragment.SmsFragment
import com.swis.android.util.ActionBarData
import com.swis.android.util.PermissionUtil


class InviteFriendActivity : MiddleBaseActivity() {

    var binding: ActivityInviteFriendBinding? = null
    /* 2 sec */



    override fun getLayoutId(): Int {
        return R.layout.activity_invite_friend
    }


    override fun onViewBinded(views: ViewDataBinding?) {
        binding= views as ActivityInviteFriendBinding?

        setStatusBarColor(resources.getColor(R.color.status_bar_color))

        binding!!.appBar.title.setAllCaps(false)
        binding!!.appBar.title.gravity=Gravity.CENTER


        binding!!.llShare.setOnClickListener {


            if(!PermissionUtil.checkSDCardPermission(mContext))
            {
                PermissionUtil.requestSDCardPermission(mContext)
            }else
            {
                loadShareView()
            }


        }

        binding!!.llShare.performClick()






        binding!!.llSms.setOnClickListener {
            if (!PermissionUtil.checkContactsPermission(mContext)) {
                PermissionUtil.requestContactPermission(mContext)
            } else {
                loadSmsView()

            }
        }
        binding!!.llEmail.setOnClickListener {
            if(!PermissionUtil.checkContactsPermission(mContext))
            {
                PermissionUtil.requestContactPermissionEmail(mContext)
            }
            else{
                loadEmailView()

            }

        }






    }




    private fun loadSmsView() {
        replaceFragment(R.id.frameContainer, SmsFragment(), false)
        setActionBarTitle("Invite Friends")
        binding!!.appBar.toolbar.setBackgroundColor(mContext.resources.getColor(R.color.white))
        binding!!.tvShare.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.tvSms.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.tvEmail.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.llMain.setBackgroundColor(Color.WHITE)
        binding!!.shareView.visibility=View.INVISIBLE
        binding!!.smsView.visibility=View.VISIBLE
        binding!!.emailView.visibility=View.INVISIBLE
    }

    private fun loadEmailView() {
        replaceFragment(R.id.frameContainer, EmailFragment(), false)
        setActionBarTitle("Invite Friends")
        binding!!.appBar.toolbar.setBackgroundColor(mContext.resources.getColor(R.color.white))
        binding!!.tvShare.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.tvSms.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.tvEmail.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        binding!!.llMain.setBackgroundColor(Color.WHITE)
        binding!!.shareView.visibility=View.INVISIBLE
        binding!!.smsView.visibility=View.INVISIBLE
        binding!!.emailView.visibility=View.VISIBLE

    }

    private fun loadShareView() {

        replaceFragment(R.id.frameContainer,ShareFragment(),false)
        setActionBarTitle("")
        binding!!.appBar.toolbar.background = resources.getDrawable(android.R.color.transparent)
        binding!!.tvShare.setTextColor(mContext.resources.getColor(R.color.white))
        binding!!.tvSms.setTextColor(mContext.resources.getColor(R.color.white))
        binding!!.tvEmail.setTextColor(mContext.resources.getColor(R.color.white))
        binding!!.llMain.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.asset_15))
        binding!!.shareView.visibility=View.VISIBLE
        binding!!.smsView.visibility=View.INVISIBLE
        binding!!.emailView.visibility=View.INVISIBLE
    }


    override fun getScreenActionTitle(): ActionBarData {
        return ActionBarData(R.drawable.back_icon,"")
    }

    override fun getToolBar(): AppActionbarBinding {
        return binding!!.appBar
    }


    companion object {
        fun start(context: Context, bundle: Bundle) {
            val intent = Intent(context, InviteFriendActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtil.CONTACTS_REQUEST_CODE -> if (PermissionUtil.checkContactsPermission(mContext)) {
                loadSmsView()

            }
            PermissionUtil.CONTACTS_REQUEST_CODE_TWO -> if (PermissionUtil.checkContactsPermission(mContext)) {
                loadEmailView()

            }

            PermissionUtil.SDCARD_REQUEST_CODE -> if (PermissionUtil.checkSDCardPermission(mContext)) {
                loadShareView()
            }


        }
    }




}
