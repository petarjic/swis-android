package com.swis.android.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.SignInActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.FragmentChangePasswordBinding;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.AppConstants;
import com.swis.android.util.UiDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordActivity extends MiddleBaseActivity implements View.OnClickListener {

   private FragmentChangePasswordBinding binding;
   private String phone;

    public ChangePasswordActivity() {
        // Required empty public constructor
    }




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (FragmentChangePasswordBinding) views;
        if(bundle!=null)
        {
            phone=bundle.getString(AppConstants.PHONE);
        }
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(checkValidation())
        {
            if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(binding.newPassword.getText()) )
            resetPassword(phone,binding.newPassword.getText().toString());
        }
    }

    private void resetPassword(String phone, String password) {
        showProgressDialog();
        Call<ApiResponse> call= ApiClient.getClient().create(ApiInterface.class).resetPassword(phone,password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
hideProgressDialog();                if(response!=null && response.body()!=null)
                {
                    if(response.body().getResponseCode()==200)
                    {
                        dialogConfirmationOk(response.body().getResponseMessage());


                    }
                    else
                        showToast(response.body().getResponseMessage());
                }
                else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
hideProgressDialog();                showToast(AppConstants.SERVER_ERROR);

            }
        });
    }

    private boolean checkValidation() {
        if(TextUtils.isEmpty(binding.newPassword.getText()))
        {
            showToast("New Password can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(binding.newPassword.getText().toString()))
        {
            return false;
        }
        if(binding.newPassword.getText().toString().equalsIgnoreCase(binding.confPassword.getText().toString()))
        {
            return true;
        }
        return false;
    }

    protected void dialogConfirmationOk(String message) {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = (TextView) dialog.findViewById(R.id.tv_yes);
        tvOkay.setText("OK");
        TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);
        tvNo.setVisibility(View.GONE);
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText(message);

        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SignInActivity.Companion.start(mContext,new Bundle());
                finishAffinity();
            }
        });
    }
}
