package com.swis.android.activity

import android.content.Context
import android.content.Intent
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface
import com.swis.android.R
import com.swis.android.base.MiddleBaseActivity
import com.swis.android.databinding.ActivitySignInBinding
import com.swis.android.model.requestmodel.UserLoginRequestModel
import com.swis.android.model.responsemodel.ApiResponse
import com.swis.android.util.AppConstants
import com.swis.android.util.PrefsHelper
import com.swis.android.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : MiddleBaseActivity() {

    var binding: ActivitySignInBinding? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_sign_in
    }

    override fun onViewBinded(views: ViewDataBinding?) {

        binding = views as ActivitySignInBinding?


        window.setBackgroundDrawable(resources.getDrawable(R.drawable.intro1))
        setStatusBarColor(resources.getColor(R.color.status_bar_color))
        if (bundle == null)
            bundle = Bundle()



        binding!!.btnSignIn.setOnClickListener {
            val email = binding!!.email.text.toString()
            val pass = binding!!.password.text.toString()

            if (email.isEmpty()) {
                binding!!.emailError.visibility = View.VISIBLE
            } else if (pass.isEmpty()) {
                binding!!.passError.visibility = View.VISIBLE
            } else {
                binding!!.emailError.visibility= View.GONE
                binding!!.passError.visibility= View.GONE
                val user = UserLoginRequestModel()
                user.email = email
                user.password = pass
                user.device_id = Util.getDeviceId(mContext)
                user.device_type = "android"
                user.device_token = prefHelper.getPref(AppConstants.DEVICE_TOKEN)

                loginUser(user)
            }

        }

        binding!!.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding!!.tvForgetPass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val bundle = Bundle()
                bundle.putBoolean(AppConstants.CHANGE_PAASWORD, true)
                val intent = Intent(mContext, MobileNumberActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, AppConstants.RequestActivity.CHANGE_MOBILE_REQUEST)
            }

        })
    }

    private fun loginUser(user: UserLoginRequestModel) {
        showProgressDialog()
        var service = ApiClient.getClientNormal().create(ApiInterface::class.java).loginUser(user)
        service.enqueue(object : Callback<ApiResponse> {


            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                hideProgressDialog()
                if (response != null && response.body() != null) {
                    if (response.body()!!.responseCode==200) {
                        prefHelper.saveUserInfo(response.body()!!.user)
                        prefHelper.savePref(AppConstants.API_TOKEN, response.body()!!.user!!.api_token)
                        prefHelper.delete(PrefsHelper.IS_LOGOUT)

                        navigateToConcernPage()

                    } else
                        showToast(response.body()!!.responseMessage)
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG)
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                hideProgressDialog()
                showToast(AppConstants.SERVER_ERROR)
            }
        })
    }


    fun navigateToConcernPage()
    {



        if (TextUtils.isEmpty(prefHelper.userInfo.phone)) run {

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.PHONE, true)
                MobileNumberActivity.start(mContext, bundle)
                finish()

        } else if (TextUtils.isEmpty(prefHelper.userInfo.bio)) run {

                val bundle = Bundle()
                bundle.putBoolean(AppConstants.BIO, true)
                ProfileActivity.start(mContext, bundle)
                finish()
            }
        else
        {
            if (bundle == null) {
                bundle = Bundle()
            }
            HomeActivity.start(mContext, bundle)
            finish()
        }

    }

    companion object {
        fun start(context: Context, bundle: Bundle) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)

        }
    }
}
