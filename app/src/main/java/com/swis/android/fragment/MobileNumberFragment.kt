/*
package com.swis.android.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import android.view.inputmethod.EditorInfo
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.CountryPickerListener
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface
import com.swis.android.R
import com.swis.android.base.BaseFragment
import com.swis.android.custom.customviews.textViews.GothamRoundedBook
import com.swis.android.databinding.ActivityMobileNumberBinding
import com.swis.android.model.responsemodel.CommonApiResponse
import com.swis.android.model.responsemodel.SendOtpApiResponse
import com.swis.android.util.AppConstants
import com.swis.android.util.PrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MobileNumberFragment : BaseFragment() {

    var binding: ActivityMobileNumberBinding? = null
    val picker = CountryPicker.newInstance("Select Country")
    var country: Country? = null
    var countryCode: String? = null
    var old_user = ""
    var isChangeNo = false
    var isChangePassword = false
    var isPhoneNoValid = false

    override fun getLayoutId(): Int {
        return R.layout.activity_mobile_number
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding = views as ActivityMobileNumberBinding?
        bundle = arguments;
        if (bundle == null) {
            bundle = Bundle()
        } else {
            isChangeNo = bundle.getBoolean(AppConstants.CHANGE_PHONE_NO)
            isChangePassword = bundle.getBoolean(AppConstants.CHANGE_PAASWORD)
        }
        if(isChangePassword)
        {
            old_user="1"
            binding!!.tvMsg.text=getString(R.string.please_provide_new)
            isPhoneNoValid=true
        }
        else {
            old_user=""
            binding!!.tvMsg.text = getString(R.string.please_provide)
        }

        activity!!.window!!.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.intro))


        country = Country.getCountryByLocale(Locale.getDefault())
        setCountryData(country!!, binding!!.tvCode)
        picker.setListener(object : CountryPickerListener {
            override fun onSelectCountry(name: String?, code: String?, dialCode: String?, flagDrawableResID: Int) {
                picker.dismiss()
                country = Country()
                country!!.setCode(code)
                country!!.setDialCode(dialCode)
                country!!.setFlag(flagDrawableResID)
                setCountryData(country!!, binding!!.tvCode)
            }

        })

        binding!!.deleteNumber.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding!!.etMobile.setText("")
            }
        })
        binding!!.llMobile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                picker.show(fragmentManager, "COUNTRY_PICKER")
            }
        })
        binding!!.etMobile.addTextChangedListener(object : TextWatcher {


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 9 && countryCode != null && !isChangePassword)
                    verifyPhone(countryCode + s.toString())
            }

        })
        binding!!.ivBack.setOnClickListener {
            if (isChangeNo || isChangePassword)
                activity!!.finish()
            else{

                if(fragmentManager!!.backStackEntryCount!=0)
                    fragmentManager!!.popBackStackImmediate()
                else
                    activity!!.finish()


            }
        }
        binding!!.etMobile.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(isPhoneNoValid)
                {
                    if (binding!!.etMobile.text?.length!! > 9 && countryCode != null)
                        sendOtp(countryCode + binding!!.etMobile.text.toString(), old_user)
                    else {
                        binding!!.emailError.text = "Please Enter Your Mobile Number"
                        binding!!.emailError.visibility = View.VISIBLE
                    }
                }



            }
            false
        }
    }

    private fun setCountryData(country: Country, tvCode: GothamRoundedBook) {
        if (country != null) {
            tvCode.setText(country.dialCode)
            countryCode = country.dialCode.replace("+", "")
            binding!!.etMobile.requestFocus()
        } else {
            tvCode.setText(Country.COUNTRIES[0].dialCode)
            countryCode = Country.COUNTRIES[0].dialCode.replace("+", "")
            binding!!.etMobile.requestFocus()

        }
    }

    private fun verifyPhone(phone: String) {

        var service = ApiClient.getClientNormal().create(ApiInterface::class.java).verifyPhone(phone)
        service.enqueue(object : Callback<CommonApiResponse> {

            override fun onResponse(call: Call<CommonApiResponse>, response: Response<CommonApiResponse>) {
                if (response != null && response.body() != null) {
                    if (response.body()!!.responseCode.equals("200", true)) {
                        isPhoneNoValid=true
                        binding!!.emailError.visibility = View.GONE
                    } else {
                        isPhoneNoValid=false
                        binding!!.emailError.text = "Your Mobile Number is already registered"
                        binding!!.emailError.visibility = View.VISIBLE
                    }


                } else
                {
                    isPhoneNoValid=false
                    showToast(AppConstants.SOMETHING_WENT_WRONG)

                }
            }

            override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {
                isPhoneNoValid=false
                showToast(AppConstants.SERVER_ERROR)
            }

        })

    }

    private fun sendOtp(mobile: String, old_user: String) {
        showProgressDialog()
        var service = ApiClient.getClient().create(ApiInterface::class.java).sendOtp(mobile, old_user)
        service.enqueue(object : Callback<SendOtpApiResponse> {

            override fun onResponse(call: Call<SendOtpApiResponse>, response: Response<SendOtpApiResponse>) {
                dismissProgressDialog()
                if (response != null && response.body() != null) {
                    if (response.body()!!.responseCode.equals("200", true)) {

                        if(!isChangePassword && !isChangeNo)
                        {
                            prefHelper.savePref(PrefsHelper.IS_Mobile_REGISTERED,true)
                        }
                        var fragment = OtpFragment()
                        bundle.putString("phone", mobile)
                        bundle.putBoolean(AppConstants.CHANGE_PHONE_NO, isChangeNo)
                        bundle.putBoolean(AppConstants.CHANGE_PAASWORD, isChangePassword)
                        fragment.arguments = bundle
                        replaceFragment(R.id.main, fragment, false)
                    } else
                        showToast(response.body()!!.responseMessage)
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG)
            }

            override fun onFailure(call: Call<SendOtpApiResponse>, t: Throwable) {
                dismissProgressDialog()
                showToast(AppConstants.SERVER_ERROR)
            }

        })
    }


}
*/
