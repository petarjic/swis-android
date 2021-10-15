package com.swis.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.activity.MobileNumberActivity;
import com.swis.android.activity.SignUpActivity;
import com.swis.android.adapter.ColorPickerAdapter;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentEditProfileBinding;
import com.swis.android.listeners.OnDateChoosenListener;
import com.swis.android.model.ColorList;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;
import com.swis.android.util.UiDialog;
import com.swis.android.util.Util;
import com.nitesh.spark.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends BaseFragment implements View.OnClickListener, OnDateChoosenListener,SparkRecyclerViewLatest.OnItemClickListener{

    private FragmentEditProfileBinding binding;
    private UserInfo userInfo;
    private String text_color;
    ArrayList<ColorList> colorLists;
    private Dialog dialog;
    private GradientDrawable bgShape;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentEditProfileBinding) views;
        bgShape = (GradientDrawable)binding.imgBar.getBackground();
        userInfo = prefHelper.getUserInfo();
        text_color=userInfo.getText_color();
        bgShape.setColor(Color.parseColor(text_color));
        binding.imgBar.setBackground(bgShape);
        binding.setUserInfo(userInfo);
        binding.etBirthdat.setOnClickListener(this);
        binding.etPhone.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.tvMore.setOnClickListener(this);
        setListenerToRootView(binding.getRoot());
        binding.colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int color) {
                bgShape.setColor(Color.parseColor(text_color));
                binding.imgBar.setBackground(bgShape);
                text_color="#"+Integer.toHexString(color);

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).showBackButton();
        ((HomeActivity) getActivity()).setTitle("Edit Profile");

    }

    @Override
    public void onStop() {
        super.onStop();
       // ((HomeActivity) getActivity()).hideBackButton();
    }

    public void setListenerToRootView(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                try {
                    if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) { // if more than 100 pixels, its probably a keyboard...
                        ((HomeActivity) getActivity()).hideOrShowBottomNavigationBar(false);
                    } else {
                        ((HomeActivity) getActivity()).hideOrShowBottomNavigationBar(true);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_birthdat:
                Calendar calendar = Calendar.getInstance();

                if(!TextUtils.isEmpty(userInfo.getDob()))
                {
                    long timestamp = Long.parseLong(Util.getTimestampFromDateString(userInfo.getDob(), AppConstants.DateFormat.DD_MM_YYYY));
                    calendar.setTimeInMillis(timestamp * 1000);
                }

                openDatePicker(calendar, v.getId(), this);
                break;

            case R.id.et_phone:
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppConstants.CHANGE_PHONE_NO, true);
                Intent intent = new Intent(mContext, MobileNumberActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.CHANGE_MOBILE_REQUEST);
                break;

            case R.id.btn_update:
                HashMap<String, String> map = getData();
                updateProfile(map);
                break;

            case R.id.tv_more:
                new ColorPickerDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton("Ok",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        setLayoutColor(envelope);
                                    }
                                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .attachAlphaSlideBar(false) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
                break;
        }
    }

    private void setLayoutColor(ColorEnvelope envelope) {
        text_color="#"+envelope.getHexCode();
        bgShape.setColor(Color.parseColor(text_color));
        binding.imgBar.setBackground(bgShape);
       // text_color=text_color;
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(binding.etName.getText().toString())) {
            map.put("name", binding.etName.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.etEmail.getText().toString())) {
            map.put("email", binding.etEmail.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.etBirthdat.getText().toString())) {
            map.put("dob", binding.etBirthdat.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.etBio.getText().toString())) {
            map.put("bio", binding.etBio.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.etCountry.getText().toString())) {
            map.put("country", binding.etCountry.getText().toString());
        }
        if (!TextUtils.isEmpty(binding.etCity.getText().toString())) {
            map.put("city", binding.etCity.getText().toString());
        }
        map.put("text_color",text_color);
        return map;
    }

    @Override
    public void onDateChoosen(Calendar calendar, int identifier) {
        binding.etBirthdat.setText(DateTimeUtils.getFormattedDate(calendar.getTimeInMillis(), AppConstants.DateFormat.DD_MM_YYYY));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.RequestActivity.CHANGE_MOBILE_REQUEST) {
            if (resultCode == RESULT_OK) {

                userInfo = prefHelper.getUserInfo();
                binding.setUserInfo(userInfo);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void updateProfile(Map<String, String> map) {
        showProgressDialog();
        Call<ApiResponse> call= ApiClient.getClient().create(ApiInterface.class).updateProfile(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if(response!=null && response.body()!=null)
                {
                    if(response.body().getResponseCode()==200)
                    {
                        dialogConfirmationOk("Your profile data updated successfully",new Confirmation());
                        prefHelper.saveUserInfo(response.body().getUser());

                    }

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        text_color=colorLists.get(position).getColorCode();
        bgShape.setColor(Color.parseColor(text_color));
        binding.imgBar.setBackground(bgShape);
        dialog.dismiss();
    }
}
