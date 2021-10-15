package com.swis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivityProfileBinding;
import com.swis.android.fragment.ProfileFragment;

public class ProfileActivity extends MiddleBaseActivity {

    private ActivityProfileBinding binding;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (ActivityProfileBinding) views;
        replaceFragment(R.id.main,new ProfileFragment(),false);
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
