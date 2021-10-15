package com.swis.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.custom.customviews.textViews.GothamRoundedBook;
import com.swis.android.databinding.ActivityMobileNumberBinding;
import com.swis.android.model.responsemodel.CommonApiResponse;
import com.swis.android.model.responsemodel.SendOtpApiResponse;
import com.swis.android.util.AppConstants;
import com.swis.android.util.CustomTextWatcher;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends MiddleBaseActivity {

    private ActivityMobileNumberBinding binding;
    CountryPicker picker =new  CountryPicker();
    Country country;
    String countryCode;
    String old_user = "";
    boolean isChangeNo = false;
    boolean isChangePassword = false;
    boolean isPhoneNoValid = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_mobile_number;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {

        binding= (ActivityMobileNumberBinding) views;
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.intro));
        if (bundle == null) {
            bundle =new Bundle();
        } else {
            isChangeNo = bundle.getBoolean(AppConstants.CHANGE_PHONE_NO);
            isChangePassword = bundle.getBoolean(AppConstants.CHANGE_PAASWORD);
        }
        if(isChangePassword)
        {
            old_user="1";
            binding.tvMsg.setText(getString(R.string.please_provide_new));
            isPhoneNoValid=true;
        }
        else {
            old_user="";
            binding.tvMsg.setText(getString(R.string.please_provide));
        }

        country = Country.getCountryByLocale(Locale.getDefault());
        setCountryData(country, binding.tvCode);

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                picker.dismiss();
                country =new Country();
                country.setCode(code);
                country.setDialCode(dialCode);
                country.setFlag(flagDrawableResID);
                setCountryData(country, binding.tvCode);
            }
        });

        binding.deleteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etMobile.setText("");

            }
        });

        binding.llMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getSupportFragmentManager(),"COUNTRY_PICKER");
            }
        });

        binding.etMobile.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.toString().length() > 9 && countryCode != null && !isChangePassword)
                    verifyPhone(countryCode + s.toString());
            }
        });


        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }

        });

        binding.etMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(isPhoneNoValid)
                    {
                        if (binding.etMobile.getText().length() > 9 && countryCode != null)
                        sendOtp(countryCode + binding.etMobile.getText().toString(), old_user);
                    else {
                        binding.emailError.setText("Please Enter Your Mobile Number");
                        binding.emailError.setVisibility(View.VISIBLE);
                    }
                    }



                }
                return false;
            }
        });


    }

    private void verifyPhone(String phone) {

        Call<CommonApiResponse> service = ApiClient.getClientNormal().create(ApiInterface.class).verifyPhone(phone);
        service.enqueue(new Callback<CommonApiResponse>() {
            @Override
            public void onResponse(Call<CommonApiResponse> call, Response<CommonApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        isPhoneNoValid = true;
                        binding.emailError.setVisibility(View.GONE);
                    } else {
                        isPhoneNoValid = false;
                        binding.emailError.setText("Your Mobile Number is already registered");
                        binding.emailError.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    isPhoneNoValid=false;
                    showToast(AppConstants.SOMETHING_WENT_WRONG);

                }
            }

            @Override
            public void onFailure(Call<CommonApiResponse> call, Throwable t) {
                isPhoneNoValid=false;
                showToast(AppConstants.SERVER_ERROR);
            }
        });

    }

    private void sendOtp(final String mobile, String old_user) {
        showProgressDialog();
        Call<SendOtpApiResponse> service = ApiClient.getClient().create(ApiInterface.class).sendOtp(mobile, old_user);
        service.enqueue(new Callback<SendOtpApiResponse>() {
            @Override
            public void onResponse(Call<SendOtpApiResponse> call, Response<SendOtpApiResponse> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {

                        if(!isChangePassword && !isChangeNo)
                        {
                            prefHelper.savePref(prefHelper.IS_Mobile_REGISTERED,true);
                        }

                        bundle.putString("phone", mobile);
                        bundle.putBoolean(AppConstants.CHANGE_PHONE_NO, isChangeNo);
                        bundle.putBoolean(AppConstants.CHANGE_PAASWORD, isChangePassword);
                        if(isChangeNo) {
                            Intent intent = new Intent(mContext, OtpActivity.class);
                            intent.putExtras(bundle);
                            startActivityForResult(intent,AppConstants.RequestActivity.FINISH_ACTIVITY);
                        }
                        else {
                            OtpActivity.start(mContext, bundle);
                        }

                    } else
                    showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<SendOtpApiResponse> call, Throwable t) {
                hideProgressDialog();
                showToast(AppConstants.SERVER_ERROR);
            }
        });


    }

    private void setCountryData(Country country, GothamRoundedBook tvCode) {
        if (country != null) {
            tvCode.setText(country.getDialCode());
            countryCode = country.getDialCode().replace("+", "");
            binding.etMobile.requestFocus();
        } else {
            tvCode.setText(Country.COUNTRIES[0].getDialCode());
            countryCode = Country.COUNTRIES[0].getDialCode().replace("+", "");
            binding.etMobile.requestFocus();

        }
    }
    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MobileNumberActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AppConstants.RequestActivity.FINISH_ACTIVITY)
        {
            if(resultCode==RESULT_OK)
            {
                Intent intent =new  Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
