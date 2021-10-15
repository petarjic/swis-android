package com.swis.android.base;


import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.Util;

public abstract class MiddleBaseActivity extends BaseActivity {
    @Override
    public void onFragmentInteraction(Object data) {

    }

    @Override
    protected void onHomeUpIconClicked() {
        onBackPressed();
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return null;
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return null;
    }


    @Override
    public void onValidationSuccess() {

    }


    @Override
    public void onValidationError() {
        Util.showAlert(mContext, "Please correct all errors.");
    }
}
