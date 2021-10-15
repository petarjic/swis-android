package com.swis.android.activity

import android.content.Context
import android.content.Intent
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.text.SpannableString

import com.swis.android.R
import com.swis.android.base.MiddleBaseActivity
import com.swis.android.databinding.ActivityMainBinding
import android.text.style.UnderlineSpan
import com.swis.android.databinding.AppActionbarBinding
import com.swis.android.util.AppConstants


class MainActivity : MiddleBaseActivity() {

    private var binding: ActivityMainBinding? = null


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }



    override fun onViewBinded(views: ViewDataBinding) {
        binding = views as ActivityMainBinding

        setStatusBarColor(resources.getColor(R.color.status_bar_color))


        var privacyText=getString(R.string.privacy_policy)
        val content = SpannableString(privacyText)
        content.setSpan(UnderlineSpan(), 0, privacyText.length, 0)
        binding!!.tvPrivacy.text=content

        var termsText=getString(R.string.terms_and_condition)
        val content1 = SpannableString(termsText)
        content1.setSpan(UnderlineSpan(), 0, termsText.length, 0)
        binding!!.tvTerms.text=content1

        if(bundle==null)
        bundle=Bundle()
        binding!!.btnSignUp.setOnClickListener {
            prefHelper.deleteAllData()

            SignUpActivity.start(mContext,bundle)}
        binding!!.btnSignIn.setOnClickListener {
            prefHelper.deleteAllData()
            SignInActivity.start(mContext,bundle)}

        binding!!.tvTerms.setOnClickListener {
            val bundle3 = Bundle()
            bundle3.putString(AppConstants.EXTRA_DATA, "term")
            bundle3.putString(AppConstants.EXTRA_TITLE, "Terms and conditions")
            CommonActivity.start(mContext,bundle3)

        }

        binding!!.tvPrivacy.setOnClickListener {
            val bundle2 = Bundle()
            bundle2.putString(AppConstants.EXTRA_DATA, "privacy")
            bundle2.putString(AppConstants.EXTRA_TITLE, "Privacy Policy")
           CommonActivity.start(mContext,bundle2)
        }

    }
    companion object {
        fun start(context: Context, bundle: Bundle) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)

        }
    }

}
