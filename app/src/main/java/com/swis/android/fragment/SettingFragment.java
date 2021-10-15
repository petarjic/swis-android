package com.swis.android.fragment;


import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.CompoundButton;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.activity.MainActivity;
import com.swis.android.activity.SearchActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentSettingBinding;
import com.swis.android.model.responsemodel.CommonApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.luongvo.widget.iosswitchview.SwitchView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener, SwitchView.OnCheckedChangeListener {

    private FragmentSettingBinding binding;
    private UserInfo userInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentSettingBinding) views;
        userInfo=prefHelper.getUserInfo();
        binding.swShare.setChecked(!userInfo.isHide_searches());
        binding.swShareLoc.setChecked(userInfo.isShare_local_search());
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).showBackButton();
        ((HomeActivity)getActivity()).setTitle("Setting");



    }

    private void setListeners() {
        binding.llEditProfile.setOnClickListener(this);
        binding.llAbout.setOnClickListener(this);
        binding.llFaq.setOnClickListener(this);
        binding.llNotifications.setOnClickListener(this);
        binding.llPrivacy.setOnClickListener(this);
        binding.llReport.setOnClickListener(this);
        binding.llTerms.setOnClickListener(this);
        binding.llLogout.setOnClickListener(this);
        binding.swShare.setOnCheckedChangeListener(this);
        binding.swShareLoc.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_profile:
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),new ProfileSettingFragment(),true);
                break;
            case R.id.ll_about:
                ((HomeActivity)getActivity()).setTitle("About");
                Bundle bundle= new Bundle();
                bundle.putString(AppConstants.EXTRA_DATA,"about");
                Fragment fragment=new CommonFragment();
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),fragment,true);
                break;
            case R.id.ll_faq:
                ((HomeActivity)getActivity()).setTitle("FAQ");
                Bundle bundle1= new Bundle();
                bundle1.putString(AppConstants.EXTRA_DATA,"faq");
                Fragment fragment1=new CommonFragment();
                fragment1.setArguments(bundle1);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),fragment1,true);
                break;
            case R.id.ll_notifications:
                ((HomeActivity)getActivity()).setTitle("Notifications");
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),new NotificationFragment(),true);
                break;
            case R.id.ll_privacy:
                ((HomeActivity)getActivity()).setTitle("Privacy Policy");
                Bundle bundle2= new Bundle();
                bundle2.putString(AppConstants.EXTRA_DATA,"privacy");
                Fragment fragment2=new CommonFragment();
                fragment2.setArguments(bundle2);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),fragment2,true);
                break;
            case R.id.ll_report:
                ((HomeActivity)getActivity()).setTitle("Report an Issue");
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),new ReportIssueFragment(),true);

                break;
            case R.id.ll_terms:
                ((HomeActivity)getActivity()).setTitle("Terms and Conditions");
                Bundle bundle3= new Bundle();
                bundle3.putString(AppConstants.EXTRA_DATA,"term");
                Fragment fragment3=new CommonFragment();
                fragment3.setArguments(bundle3);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(),fragment3,true);
                break;
            case R.id.ll_logout:
                dialogConfirmation("Are you sure you want to logout",new Confirmation(){
                    @Override
                    public void onYes() {
                        logout();
                        super.onYes();
                    }

                    @Override
                    public void onNo() {
                        super.onNo();
                    }
                });
                break;
        }
    }

    private void logout() {
        showProgressDialog();
        Call<CommonApiResponse> call= ApiClient.getClient().create(ApiInterface.class).logout();
        call.enqueue(new Callback<CommonApiResponse>() {
            @Override
            public void onResponse(Call<CommonApiResponse> call, Response<CommonApiResponse> response) {
                dismissProgressDialog();
                if(response!=null && response.body()!=null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        prefHelper.deleteAllData();
                        getActivity().finishAffinity();
                        MainActivity.Companion.start(mContext,new Bundle());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonApiResponse> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }


   /* @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Map<String,String> map=new HashMap<>();

        switch (buttonView.getId())
        {
            case R.id.sw_share:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("hide_searches",isChecked ? "off":"on");
                updateProfileData(map);
                if(!isChecked)
                {
                    binding.swShareLoc.setChecked(false);
                    map.clear();
                    map.put("share_local_search",isChecked ? "1":"0");
                    updateProfileData(map);
                }
                break;

                case R.id.sw_share_loc:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("share_local_search",isChecked ? "1":"0");
                updateProfileData(map);
                break;

        }

    }
*/
    @Override
    public void onCheckedChanged(SwitchView switchView, boolean isChecked) {
        Map<String,String> map=new HashMap<>();

        switch (switchView.getId())
        {
            case R.id.sw_share:
                map.clear();
                map.put("hide_searches",isChecked ? "off":"on");
                updateProfileData(map);
                if(!isChecked)
                {
                    binding.swShareLoc.setChecked(false);
                    map.clear();
                    map.put("share_local_search",isChecked ? "1":"0");
                    updateProfileData(map);
                }
                break;

            case R.id.sw_share_loc:

                map.clear();
                map.put("share_local_search",isChecked ? "1":"0");
                updateProfileData(map);
                break;

        }
    }
}
