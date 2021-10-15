package com.swis.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivitySignUpBinding;
import com.swis.android.model.requestmodel.UserLoginRequestModel;
import com.swis.android.model.requestmodel.UserRegisterRequestModel;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.CommonApiResponse;
import com.swis.android.model.responsemodel.UserRegisterResponseModel;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;
import com.swis.android.util.CustomTextWatcher;
import com.swis.android.util.Util;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends MiddleBaseActivity implements View.OnClickListener {

    private ActivitySignUpBinding binding;
    private boolean isUpdate;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivitySignUpBinding) views;

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.intro1));

        String signInText = getString(R.string.already_have_an_account_sign_in);

        SpannableString spannableString = new SpannableString(signInText);


        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                bundle = new Bundle();
                SignInActivity.Companion.start(mContext, bundle);
            }
        }, signInText.length() - 7, signInText.length(), 0);


        binding.signIn.setText(spannableString);

        binding.signIn.setMovementMethod(LinkMovementMethod.getInstance());

        binding.userName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals(" ")) { // for backspace
                            return "_";
                        }
                        return cs;
                    }
                },new InputFilter.LengthFilter(40)
        });
        if (TextUtils.isEmpty(prefHelper.getUserInfo().getName())) {
            binding.userName.addTextChangedListener(new CustomTextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    super.onTextChanged(s, start, before, count);
                    String text=s.toString();
                    if(text.length()>0)
                        verifyUserName(text);
                    else
                        binding.userNameError.setVisibility(View.GONE);

                }


            });

            binding.email.addTextChangedListener(new CustomTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()>0)
                    verifyEmail(s.toString());
                    else {
                        binding.emailError.setText("Please Enter Correct Email Id");

                        binding.emailError.setVisibility(View.VISIBLE);

                    }
                }
            });
        }

        binding.btnSignUp.setOnClickListener(this);
    }

    private void verifyEmail(String email) {
        Call<CommonApiResponse> service = ApiClient.getClientNormal().create(ApiInterface.class).verifyEmail(email);
        service.enqueue(new Callback<CommonApiResponse>() {
            @Override
            public void onResponse(Call<CommonApiResponse> call, Response<CommonApiResponse> response) {
                if(response!=null && response.body()!=null && response.code()==200) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        binding.emailError.setVisibility(View.GONE);
                    } else {
                        binding.emailError.setText("Please Enter Correct Email Id");

                        binding.emailError.setVisibility(View.VISIBLE);

                    }
                }
                else {
                    binding.emailError.setText("Please Enter Correct Email Id");

                    binding.emailError.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<CommonApiResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(prefHelper.getUserInfo().getUsername())) {
            binding.password.setEnabled(false);
            binding.confPass.setEnabled(false);
            binding.userName.setEnabled(false);
            isUpdate = true;
        }

    }

    private void verifyUserName(String u_name) {

        Call<CommonApiResponse> service = ApiClient.getClientNormal().create(ApiInterface.class).verifyUserName(u_name);
        service.enqueue(new Callback<CommonApiResponse>() {
            @Override
            public void onResponse(Call<CommonApiResponse> call, Response<CommonApiResponse> response) {
                if(response!=null && response.body()!=null && response.code()==200){
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        binding.userNameError.setVisibility(View.GONE);
                    } else {
                        binding.userNameError.setVisibility(View.VISIBLE);

                    }
                }
                else {

                    binding.userNameError.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<CommonApiResponse> call, Throwable t) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if (!binding.cbPolicy.isChecked()) {
            showToast("Please accept our terms and conditions");
            return;

        }

        if (TextUtils.isEmpty(binding.userName.getText())) {
            binding.userNameError.setText("Please Enter Username");
            binding.userNameError.setVisibility(View.VISIBLE);
            return;

        } else
            binding.userNameError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(binding.fullName.getText())) {
            binding.nameError.setText("Please Enter Full Name");
            binding.nameError.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.nameError.setVisibility(View.GONE);

        }

        if (TextUtils.isEmpty(binding.email.getText())) {
            binding.emailError.setText("Please Enter Email");
            binding.emailError.setVisibility(View.VISIBLE);
            return;

        } else {
            binding.emailError.setVisibility(View.GONE);

        }
        if (TextUtils.isEmpty(binding.password.getText())) {
            binding.passError.setText("Please Enter Password");
            binding.passError.setVisibility(View.VISIBLE);
            return;

        } else {
            binding.passError.setVisibility(View.GONE);

        }
        if (getText(binding.password).length()<6) {
            binding.passError.setText("Password should be at least 6 digits");
            binding.passError.setVisibility(View.VISIBLE);
            return;

        } else {
            binding.passError.setVisibility(View.GONE);

        }
        if (!binding.password.getText().toString().equals(binding.confPass.getText().toString())) {
            binding.confPassError.setText("Repeat password does not match");
            binding.confPassError.setVisibility(View.VISIBLE);
            return;
        } else {
            binding.confPassError.setVisibility(View.GONE);

        }


        if (!isUpdate) {
            UserRegisterRequestModel userData = new UserRegisterRequestModel();
            userData.setName(binding.fullName.getText().toString());
            userData.setUsername(binding.userName.getText().toString());
            userData.setEmail(binding.email.getText().toString());
            userData.setPassword(binding.password.getText().toString());
            userData.setDevice_type("android");
            userData.setDevice_token((String) prefHelper.getPref(AppConstants.DEVICE_TOKEN));
            userData.setDevice_id(Util.getDeviceId(mContext));
            registerUser(userData);
        } else {
            HashMap<String, String> map = getData();
            updateProfile(map);
        }

    }

    protected void updateProfile(Map<String, String> map) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).updateProfile(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        prefHelper.saveUserInfo(response.body().getUser());
                        Intent intent = new Intent(mContext, MobileNumberActivity.class);
                        startActivity(intent);
                    }

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(binding.fullName.getText().toString())) {
            map.put("name", binding.fullName.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.email.getText().toString())) {
            map.put("email", binding.email.getText().toString());
        }


        return map;
    }


    private void registerUser(UserRegisterRequestModel userData) {
        showProgressDialog();
        Call<UserRegisterResponseModel> service = ApiClient.getClientNormal().create(ApiInterface.class).registerUser(userData);
        service.enqueue(new Callback<UserRegisterResponseModel>() {
            @Override
            public void onResponse(Call<UserRegisterResponseModel> call, Response<UserRegisterResponseModel> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        prefHelper.saveUserInfo(response.body().getUser());
                        prefHelper.savePref(AppConstants.API_TOKEN, response.body().getUser().getApi_token());
                        Intent intent = new Intent(mContext, MobileNumberActivity.class);
                        startActivity(intent);

                    } else {
                        showToast(response.body().getResponseMessage());
                    }
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);

            }

            @Override
            public void onFailure(Call<UserRegisterResponseModel> call, Throwable t) {
                hideProgressDialog();
                showToast(AppConstants.SERVER_ERROR);
            }
        });

    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
