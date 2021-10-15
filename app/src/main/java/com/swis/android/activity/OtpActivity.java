package com.swis.android.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivityOtpBinding;
import com.swis.android.listeners.CustomTextChangeListener;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.model.responsemodel.UserRegisterResponseModel;
import com.swis.android.util.AppConstants;
import com.swis.android.util.UiDialog;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends MiddleBaseActivity {
    private ActivityOtpBinding binding;
    String phone;
    boolean isChangeNo = false;
    boolean isChangePassword = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_otp;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (ActivityOtpBinding) views;
        if(bundle!=null)
        {
            isChangeNo = bundle.getBoolean(AppConstants.CHANGE_PHONE_NO);
            isChangePassword = bundle.getBoolean(AppConstants.CHANGE_PAASWORD);
        }
        phone = bundle.getString("phone");
        binding.code1.addTextChangedListener(new CustomTextChangeListener(){
            @Override
            public void afterTextChanged(@Nullable Editable s) {
                super.afterTextChanged(s);
                if (s.toString().length() > 0)
                    binding.code2.requestFocus();
            }
        }); binding.code2.addTextChangedListener(new CustomTextChangeListener(){
            @Override
            public void afterTextChanged(@Nullable Editable s) {
                super.afterTextChanged(s);
                if (s.toString().length() > 0)
                    binding.code3.requestFocus();
            }
        }); binding.code3.addTextChangedListener(new CustomTextChangeListener(){
            @Override
            public void afterTextChanged(@Nullable Editable s) {
                super.afterTextChanged(s);
                if (s.toString().length() > 0)
                    binding.code4.requestFocus();
            }
        });

        binding.code4.addTextChangedListener(new CustomTextChangeListener(){
            @Override
            public void afterTextChanged(@Nullable Editable s) {
                super.afterTextChanged(s);
                if (!TextUtils.isEmpty(s.toString()))
                {
                    if ((!TextUtils.isEmpty(binding.code1.getText())) && (!TextUtils.isEmpty(binding.code2.getText())) && (!TextUtils.isEmpty(binding.code3.getText())) && (!TextUtils.isEmpty(binding.code4.getText()))){
                    String otp = binding.code1.getText().toString() + binding.code2.getText().toString() + binding.code3.getText().toString() + binding.code4.getText().toString();
                    verifyOtp(otp.trim());
                }
                }
            }
        });


        binding.code2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding.code1.requestFocus();
                } return false;
            }
        });
        binding.code3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding.code2.requestFocus();
                } return false;
            }
        });

        binding.code4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL)
                {
                    binding.code3.requestFocus();
                } return false;
            }
        });




        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void verifyOtp(String otp) {
        showProgressDialog();
        retrofit2.Call<UserRegisterResponseModel> service = ApiClient.getClient().create(ApiInterface.class).otpVerify(otp, this.phone);
        service.enqueue(new Callback<UserRegisterResponseModel>() {
            @Override
            public void onResponse(Call<UserRegisterResponseModel> call, Response<UserRegisterResponseModel> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {

                        if (isChangeNo) {
                            UserInfo userInfo = prefHelper.getUserInfo();
                            userInfo.setPhone(response.body().getUser().getPhone());
                            prefHelper.saveUserInfo(userInfo);
                            dialogConfirmationOk("Phone number successfully updated");
                        } else if (isChangePassword) {
                            Intent intent=new Intent(mContext, ChangePasswordActivity.class);
                            Bundle bundle =new  Bundle();
                            bundle.putString(AppConstants.PHONE, phone);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            UserInfo userInfo = prefHelper.getUserInfo();
                            userInfo.setPhone(response.body().getUser().getPhone());
                            prefHelper.saveUserInfo(userInfo);
                            Intent intent=new Intent(mContext,ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }


                    } else {
                        showToast(response.body().getResponseMessage());
                    }
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<UserRegisterResponseModel> call, Throwable t) {
                hideProgressDialog();
                showToast(AppConstants.SOMETHING_WENT_WRONG);

            }
        });

        /*service.enqueue(object : Callback<UserRegisterResponseModel> {

            override fun onResponse(call: Call<UserRegisterResponseModel>, response: Response<UserRegisterResponseModel>) {

            }

            override fun onFailure(call: Call<UserRegisterResponseModel>, t: Throwable) {
                dismissProgressDialog()
                showToast(AppConstants.SERVER_ERROR)

            }


        })*/
    }

    protected void dialogConfirmationOk(String message) {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = dialog.findViewById(R.id.tv_yes);
        tvOkay.setText("OK");
        TextView tvNo = dialog.findViewById(R.id.tv_no);
        tvNo.setVisibility(View.GONE);
        TextView tvText = dialog.findViewById(R.id.tv_text);
        tvText.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent intent =new  Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, OtpActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
