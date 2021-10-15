package com.swis.android.fragment;

import androidx.databinding.ViewDataBinding;

import android.widget.CompoundButton;

import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentNotificationBinding;
import com.swis.android.model.responsemodel.UserInfo;

import java.util.HashMap;
import java.util.Map;


public class NotificationFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentNotificationBinding binding;
    private UserInfo userInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (FragmentNotificationBinding) views;
        userInfo = prefHelper.getUserInfo();
        binding.swComments.setChecked(userInfo.isNotification_comment());
        binding.swFollow.setChecked(userInfo.isNotification_follow());
        binding.swLikes.setChecked(userInfo.isNotification_like());

        binding.swLikes.setOnCheckedChangeListener(this);
        binding.swFollow.setOnCheckedChangeListener(this);
        binding.swComments.setOnCheckedChangeListener(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity)getActivity()).setTitle("Setting");

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Map<String, String> map = new HashMap<>();

        switch (buttonView.getId()) {
            case R.id.sw_likes:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("notification_like",isChecked ? "on":"off");
                updateProfileData(map);
                break;
            case R.id.sw_follow:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("notification_follow",isChecked ? "on":"off");
                updateProfileData(map);
                break;
            case R.id.sw_comments:
                buttonView.setChecked(isChecked);
                map.clear();
                map.put("notification_comment",isChecked ? "on":"off");
                updateProfileData(map);
                break;

        }
    }
}
