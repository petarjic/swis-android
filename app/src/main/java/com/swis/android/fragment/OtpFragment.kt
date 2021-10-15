/*
package com.swis.android.fragment

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.databinding.ViewDataBinding
import android.widget.TextView
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface
import com.swis.android.R
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.ActivityOtpBinding
import com.swis.android.listeners.CustomTextChangeListener
import com.swis.android.model.responsemodel.UserRegisterResponseModel
import com.swis.android.util.AppConstants
import com.swis.android.util.UiDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpFragment : BaseFragment() {

    var binding: ActivityOtpBinding? = null
    var phone: String? = null
    var isChangeNo = false
    var isChangePassword = false


    override fun getLayoutId(): Int {
        return R.layout.activity_otp
    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding = views as ActivityOtpBinding?
        bundle = arguments
        if(bundle!=null)
        {
            isChangeNo = bundle.getBoolean(AppConstants.CHANGE_PHONE_NO)
            isChangePassword = bundle.getBoolean(AppConstants.CHANGE_PAASWORD)
        }
        phone = arguments!!.getString("phone")
        binding!!.code1.addTextChangedListener(object : CustomTextChangeListener() {

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0)
                    binding!!.code2.requestFocus()
            }
        })

        binding!!.code2.addTextChangedListener(object : CustomTextChangeListener() {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0)
                    binding!!.code3.requestFocus()

            }
        })
        binding!!.code3.addTextChangedListener(object : CustomTextChangeListener() {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0)
                    binding!!.code4.requestFocus()

            }
        })

        binding!!.code4.addTextChangedListener(object : CustomTextChangeListener() {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s.toString()))
                {
                    if (!binding!!.code1.text!!.isNullOrEmpty() && !binding!!.code2.text!!.isNullOrEmpty() && !binding!!.code3.text!!.isNullOrEmpty() && !binding!!.code4.text!!.isNullOrEmpty()) {
                        var otp = binding!!.code1.text.toString() + binding!!.code2.text.toString() + binding!!.code3.text.toString() + binding!!.code4.text.toString()
                        verifyOtp(otp.trim())
                    }
                }

            }
        })


        binding!!.code2.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event!!.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding!!.code1.requestFocus()
                }
                return false
            }

        })
        binding!!.code3.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event!!.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding!!.code2.requestFocus()
                }
                return false
            }

        })
        binding!!.code4.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event!!.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding!!.code3.requestFocus()
                }

                return false
            }

        })

        */
/*binding !!.code4.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding!!.code1.text!!.isNullOrEmpty() && !binding!!.code2.text!!.isNullOrEmpty() && !binding!!.code3.text!!.isNullOrEmpty() && !binding!!.code4.text!!.isNullOrEmpty()) {
                    var otp = binding!!.code1.text.toString() + binding!!.code2.text.toString() + binding!!.code3.text.toString() + binding!!.code4.text.toString()
                    verifyOtp(otp.trim())
                }


            }

            false
        }*//*


        binding!!.ivBack.setOnClickListener {
            fragmentManager!!.popBackStackImmediate()
        }


    }

    private fun verifyOtp(otp: String) {
        showProgressDialog()
        var service = ApiClient.getClient().create(ApiInterface::class.java).otpVerify(otp, this.phone!!)
        service.enqueue(object : Callback<UserRegisterResponseModel> {

            override fun onResponse(call: Call<UserRegisterResponseModel>, response: Response<UserRegisterResponseModel>) {
                dismissProgressDialog()
                if (response != null && response.body() != null) {
                    if (response.body()!!.responseCode.equals("200", true)) {

                            if (isChangeNo) {
                                var userInfo = prefHelper.userInfo
                                userInfo.phone = response.body()!!.user!!.phone
                                prefHelper.saveUserInfo(userInfo)
                                dialogConfirmationOk("Phone number successfully updated")
                            } else if (isChangePassword) {
                                var fragment = ChangePasswordActivity();
                                var bundle = Bundle();
                                bundle.putString(AppConstants.PHONE, phone)
                                fragment.arguments = bundle
                                replaceFragment(R.id.main, fragment, false)
                            } else {
                                var userInfo = prefHelper.userInfo
                                userInfo.phone = response.body()!!.user!!.phone
                                prefHelper.saveUserInfo(userInfo)
                                replaceFragment(R.id.main, ProfileFragment(), false)

                            }


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

    protected fun dialogConfirmationOk(message: String) {
        val dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm)
        dialog.show()
        val tvOkay = dialog.findViewById(R.id.tv_yes) as TextView
        tvOkay.text = "OK"
        val tvNo = dialog.findViewById(R.id.tv_no) as TextView
        tvNo.visibility = View.GONE
        val tvText = dialog.findViewById(R.id.tv_text) as TextView
        tvText.text = message
        tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(dialog: DialogInterface?) {
                var intent = Intent()
                activity!!.setResult(RESULT_OK, intent)
                activity!!.finish()
            }
        })
    }

}
*/
