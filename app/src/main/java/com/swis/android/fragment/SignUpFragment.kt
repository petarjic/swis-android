/*
package com.swis.android.fragment


import android.os.Bundle
import android.text.Editable
import androidx.databinding.ViewDataBinding
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface

import com.swis.android.R
import com.swis.android.activity.SignInActivity
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentSignUpBinding
import com.swis.android.model.requestmodel.UserRegisterRequestModel
import com.swis.android.model.responsemodel.CommonApiResponse
import com.swis.android.model.responsemodel.UserRegisterResponseModel
import com.swis.android.util.AppConstants
import com.swis.android.util.PrefsHelper
import com.swis.android.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpFragment : BaseFragment() {

    var binding: FragmentSignUpBinding? = null
    var validate = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_sign_up
    }


    override fun onViewBinded(views: ViewDataBinding?) {

        binding = views as FragmentSignUpBinding?

        activity!!.window!!.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.intro1))

        var signInText = getString(R.string.already_have_an_account_sign_in)

        val spannableString = SpannableString(signInText)



        binding!!.ivBack.setOnClickListener {
            this.activity!!.finish()
        }



        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val bundle= Bundle()
                SignInActivity.start(mContext, bundle)
                activity!!.finish()
            }
        }, signInText.length - 7, signInText.length, 0)

        binding!!.signIn.setText(spannableString)

        binding!!.signIn.setMovementMethod(LinkMovementMethod.getInstance())

        binding!!.userName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isNullOrEmpty())

                    verifyUserName(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })


        binding!!.email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                verifyEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding!!.btnSignUp.setOnClickListener {

            if(!binding!!.cbPolicy.isChecked)
            {
                showToast("Please accept our terms and conditions")
                return@setOnClickListener

            }

            if (binding!!.userName.text.isNullOrEmpty())
            {
                binding!!.userNameError.text="Please Enter Username"
                binding!!.userNameError.visibility=View.VISIBLE
                return@setOnClickListener

            }
            else
                binding!!.userNameError.visibility=View.GONE

            if (binding!!.fullName.text.isNullOrEmpty())
            {
                binding!!.nameError.text="Please Enter Full Name"
                binding!!.nameError.visibility=View.VISIBLE
                return@setOnClickListener
            }
            else
            {
                binding!!.nameError.visibility=View.GONE

            }

            if (binding!!.email.text.isNullOrEmpty())
            {
                binding!!.emailError.text="Please Enter Email"
                binding!!.emailError.visibility=View.VISIBLE
                return@setOnClickListener

            }
            else
            {
                binding!!.emailError.visibility=View.GONE

            }
             if (binding!!.password.text.isNullOrEmpty())
            {
                binding!!.passError.text="Please Enter Password"
                binding!!.passError.visibility=View.VISIBLE
                return@setOnClickListener

            }
            else
             {
                 binding!!.passError.visibility=View.GONE

             }
             if (!binding!!.password.text.toString().equals(binding!!.confPass.text.toString(), true))
            {
                binding!!.confPassError.text="Password and Confirm Password should match"
                binding!!.confPassError.visibility=View.VISIBLE
                return@setOnClickListener

            }
            else
             {
                 binding!!.confPassError.visibility=View.GONE

             }



                val userData = UserRegisterRequestModel()
                userData.name = binding!!.fullName.text.toString()
                userData.username = binding!!.userName.text.toString()
                userData.email = binding!!.email.text.toString()
                userData.password = binding!!.password.text.toString()
                userData.device_type = "android"
                userData.device_token = prefHelper.getPref(AppConstants.DEVICE_TOKEN)
                userData.device_id = Util.getDeviceId(mContext)
                registerUser(userData)



            //registerUser()

        }
    }


    private fun registerUser(userData: UserRegisterRequestModel) {

        showProgressDialog()

        var service = ApiClient.getClientNormal().create(ApiInterface::class.java).registerUser(userData)

        service.enqueue(object : Callback<UserRegisterResponseModel> {
            override fun onResponse(call: Call<UserRegisterResponseModel>, response: Response<UserRegisterResponseModel>) {
                dismissProgressDialog()
                if (response != null && response.body() != null) {
                    if (response.body()!!.responseCode.equals("200", true)) {
                        prefHelper.saveUserInfo(response.body()!!.user)
                        prefHelper.savePref(AppConstants.API_TOKEN, response.body()!!.user!!.api_token)
                       // prefHelper.savePref(PrefsHelper.IS_LOGIN,true)
                        replaceFragment(R.id.main, MobileNumberFragment(), true)
                    } else {
                        showToast(response.body()!!.responseMessage)
                    }
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG)


            }

            override fun onFailure(call: Call<UserRegisterResponseModel>, t: Throwable) {
                dismissProgressDialog()
                showToast(AppConstants.SERVER_ERROR)
            }
        })

    }

    private fun verifyUserName(u_name: String) {

        var service = ApiClient.getClientNormal().create(ApiInterface::class.java).verifyUserName(u_name)

        service.enqueue(object : Callback<CommonApiResponse> {
            override fun onResponse(call: Call<CommonApiResponse>, response: Response<CommonApiResponse>) {
                if (response.body()?.responseCode.equals("200", true)) {
                    binding!!.userNameError.visibility = View.GONE
                } else {
                    binding!!.userNameError.visibility = View.VISIBLE

                }

            }

            override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {

            }
        })

    }


    private fun verifyEmail(email: String) {

        var service = ApiClient.getClientNormal().create(ApiInterface::class.java).verifyEmail(email)

        service.enqueue(object : Callback<CommonApiResponse> {
            override fun onResponse(call: Call<CommonApiResponse>, response: Response<CommonApiResponse>) {
                if (response.body()?.responseCode.equals("200", true)) {
                    binding!!.emailError.visibility = View.GONE
                } else {
                    binding!!.emailError.text = "Please Enter Correct Email Id"
                    binding!!.emailError.visibility = View.VISIBLE

                }

            }

            override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {

            }
        })

    }
}
*/
