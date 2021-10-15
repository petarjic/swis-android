package com.swis.android.fragment;


import android.text.TextUtils;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentCommonBinding;
import com.swis.android.databinding.FragmentReportIssueBinding;
import com.swis.android.model.responsemodel.CommonApiResponse;
import com.swis.android.util.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportIssueFragment extends BaseFragment {

    private FragmentReportIssueBinding binding;

    public ReportIssueFragment() {
        // Required empty public constructor
    }




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report_issue;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (FragmentReportIssueBinding) views;
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=binding.etMessage.getText().toString();
                if(!TextUtils.isEmpty(str))
                {
                    reportIssue(str);
                }
            }
        });

    }

    private void reportIssue(String str) {
        showProgressDialog();
        Call<CommonApiResponse> call=ApiClient.getClient().create(ApiInterface.class).reportIssue(str);
        call.enqueue(new Callback<CommonApiResponse>() {
            @Override
            public void onResponse(Call<CommonApiResponse> call, Response<CommonApiResponse> response) {
                dismissProgressDialog();
                if(response!=null && response.body()!=null)
                {
                    if(response.body().getResponseCode().equalsIgnoreCase("200"))
                    {
                        showToast(response.body().getResponseMessage());
                    }
                    else
                        showToast(response.body().getResponseMessage());

                }
                else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<CommonApiResponse> call, Throwable t) {
                dismissProgressDialog();
                showToast(AppConstants.SERVER_ERROR);

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity)getActivity()).setTitle("Setting");

    }

}
