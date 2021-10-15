package com.swis.android.fragment;


import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;

import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentProfileSettingBinding;
import com.swis.android.databinding.FragmentSettingBinding;
import com.swis.android.listeners.FollowRequestUpdateListeners;
import com.swis.android.listeners.HomePageFollowersRefreshListeners;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;

import java.util.HashMap;
import java.util.Map;

import vn.luongvo.widget.iosswitchview.SwitchView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettingFragment extends BaseFragment implements View.OnClickListener, SwitchView.OnCheckedChangeListener {

    private FragmentProfileSettingBinding binding;
    private UserInfo userInfo;
    private boolean isFromProfile;
    private FollowRequestUpdateListeners refreshListeners;

    public ProfileSettingFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile_setting;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentProfileSettingBinding) views;
        bundle=getArguments();
        if(bundle!=null)
        {
            isFromProfile=bundle.getBoolean(AppConstants.EXTRA_DATA);
        }
        userInfo = prefHelper.getUserInfo();
        binding.swShareSearch.setChecked(!userInfo.isHide_searched());
        binding.swShareBookmarks.setChecked(!userInfo.isHide_favourite());
        binding.swAutoAccept.setChecked(userInfo.isAuto_accept());
        updateRequestCount(userInfo);

        refreshListeners = new FollowRequestUpdateListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateRequestCount(prefHelper.getUserInfo());
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(refreshListeners, new IntentFilter(AppConstants.REFRESH_FOLLOW_REQUEST));

        setListeners();
    }

    private void updateRequestCount(UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.getFollow_request_count())) {
            String str = getString(R.string.follow_request);
            String str1 = " (" + userInfo.getFollow_request_count() + ")";
            String text = str + str1;
            ForegroundColorSpan fcs = new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue));
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(fcs, text.indexOf(str1) + 1, text.indexOf(str1) + str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvFollowReq.setText(spannableString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).showBackButton();
        ((HomeActivity)getActivity()).setTitle("Profile");



    }

    private void setListeners() {
        binding.llBio.setOnClickListener(this);
        binding.llBackImg.setOnClickListener(this);
        binding.llFollowRequest.setOnClickListener(this);
        binding.llProfilePic.setOnClickListener(this);
        binding.llFollowRequest.setOnClickListener(this);
        binding.swAutoAccept.setOnCheckedChangeListener(this);
        binding.swShareBookmarks.setOnCheckedChangeListener(this);
        binding.swShareSearch.setOnCheckedChangeListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bio:
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), new EditProfileFragment(), true);
                break;
            case R.id.ll_back_img:
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), new ChangeBackgroundImageFragment(), true);
                break;
            case R.id.ll_profile_pic:
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), new ChangeProfilePicFragment(), true);
                break;
            case R.id.ll_follow_request:
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), new FollowRequestFragment(), true);
                break;

        }
    }

    /*@Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Map<String, String> map = new HashMap<>();

        switch (buttonView.getId()) {
            case R.id.sw_auto_accept:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("auto_accept",isChecked ? "on":"off");
                updateProfileData(map);
                break;
            case R.id.sw_share_search:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("hide_searched",isChecked ? "off":"on");
                updateProfileData(map);
                break;
            case R.id.sw_share_bookmarks:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("hide_favourite",isChecked ? "off":"on");
                updateProfileData(map);
                break;

        }
    }*/

    @Override
    public void onStop() {
        super.onStop();
        if(isFromProfile)
        ((HomeActivity)getActivity()).hideBackButton();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refreshListeners);

    }

    @Override
    public void onCheckedChanged(SwitchView buttonView, boolean isChecked) {
        Map<String, String> map = new HashMap<>();

        switch (buttonView.getId()) {
            case R.id.sw_auto_accept:
              //  buttonView.setChecked(isChecked);
                map.clear();
                map.put("auto_accept",isChecked ? "on":"off");
                updateProfileData(map);
                break;
            case R.id.sw_share_search:
              //  buttonView.setChecked(isChecked);
                map.clear();
                map.put("hide_searched",isChecked ? "off":"on");
                updateProfileData(map);
                break;
            case R.id.sw_share_bookmarks:
              //  buttonView.setChecked(isChecked);
                map.clear();
                map.put("hide_favourite",isChecked ? "off":"on");
                updateProfileData(map);
                break;

        }
    }
}
