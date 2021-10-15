package com.swis.android.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.swis.android.App;
import com.swis.android.R;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.listeners.LoadFileListener;
import com.swis.android.listeners.OnFragmentInteractionListener;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.Confirmation;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.UiDialog;
import com.swis.android.util.Validator;

public abstract class BaseActivity extends AppCompatActivity implements OnFragmentInteractionListener, Validator.ValidationListener {
    protected Bundle bundle;
    protected PrefsHelper prefHelper;
    protected Context mContext;
    private Dialog dialog;
    private AppActionbarBinding toolBar;
    protected Validator validator;
    private LoadFileListener loadFileListener;
    private Uri capturedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        bundle = getIntent().getExtras();

        mContext = this;
        prefHelper = App.getInstance().getPrefsHelper();
        ViewDataBinding views = DataBindingUtil.setContentView(this, getLayoutId());
        validator = new Validator(views);
        validator.setValidationListener(this);
        onViewBinded(views);
        toolBar = getToolBar();
        if(toolBar!=null){
            setSupportActionBar(toolBar.toolbar);
            toolBar.setActionBar(getScreenActionTitle());
            toolBar.actionBarHomeup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHomeUpIconClicked();
                }
            });

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        }
    }
    protected void setActionBarImage(int icon){
        if(toolBar!=null){
            ActionBarData actionBarData = toolBar.getActionBar();
            if(actionBarData!=null){
                actionBarData.setSubsIcon(icon);
                toolBar.setActionBar(actionBarData);
            }
        }
    }

    protected abstract void onHomeUpIconClicked();

    protected abstract ActionBarData getScreenActionTitle();

    protected abstract AppActionbarBinding getToolBar();

    protected abstract int getLayoutId();

    protected abstract void onViewBinded(ViewDataBinding views);

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);

    }

    protected String getText(EditText etName) {
        return etName.getText().toString().trim();
    }

    protected Dialog showProgressDialog(){
        dialog = UiDialog.getDialogFixed(this, R.layout.center_progress);
       // Glide.with(this).load(R.drawable.loader_small).placeholder(R.drawable.default_user).into(new GlideDrawableImageViewTarget((ImageView) dialog.findViewById(R.id.image_progress)));
        if(!isFinishing())
            dialog.show();
        return dialog;
    }
    protected void hideProgressDialog() {
        if (mContext != null && !((Activity) mContext).isFinishing() && dialog != null)
            dialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }


    protected void setActionBarTitle(String title) {
        if (toolBar != null) {
            ActionBarData actionBarData = toolBar.getActionBar();
            if (actionBarData != null) {
                actionBarData.setTitle(title);
                toolBar.setActionBar(actionBarData);
            }
        }
    }

    public String getActionBarTitle() {
        if (toolBar != null) {
            ActionBarData actionBarData = toolBar.getActionBar();
            if (actionBarData != null) {
              return actionBarData.getTitle();
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    protected void showToast(String s) {
        Toast.makeText(mContext,s, Toast.LENGTH_SHORT).show();
    }


    protected void shareContent(String msg) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "NisinEdu - Question");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share this question via..."));
    }

    protected void shareApp() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "helloo");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share this App via..."));
    }


    protected void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    protected void addFragment(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(containerId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    public void replaceFragmentWithAnimation(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(containerId, fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    protected void addFragmentWithAnimation(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.add(containerId, fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    protected void setStatusBarColor(int color)
    {
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    protected void dialogConfirmation(String message, final Confirmation listener) {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onYes();
                dialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNo();
                dialog.dismiss();
            }
        });
    }

    protected void displayLogoutConfirmation() {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText("Are you sure, You want to Exit?");
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   prefHelper.setIsLoggedIn(false);
             //   LoginActivity.start(mContext,new Bundle(), Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                dialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    protected void deleteFavourite() {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);
        TextView tvText = (TextView) dialog.findViewById(R.id.tv_text);
        tvText.setText("Are you sure, You want to delete?");
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("successfull");
                finish();
                dialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}