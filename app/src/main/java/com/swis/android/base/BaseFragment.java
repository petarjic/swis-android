package com.swis.android.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.App;
import com.swis.android.R;
import com.swis.android.listeners.OnDateChoosenListener;
import com.swis.android.listeners.OnFragmentInteractionListener;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.Confirmation;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.UiDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZIploan-Nitesh on 07/10/2016.
 */
public abstract class BaseFragment extends Fragment {
    protected OnFragmentInteractionListener mListener;
    private Dialog dialog;
    protected PrefsHelper prefHelper;
    protected Context mContext;
    protected Bundle bundle;
    private ViewDataBinding views;
    private TextToSpeech tts;
    private int theme;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefHelper = App.getInstance().getPrefsHelper();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        views = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        onViewBinded(views);
        return views.getRoot();
    }

    protected abstract int getLayoutId();

    protected abstract void onViewBinded(ViewDataBinding views);

    protected boolean isAPICallNeeded(int type) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long difference = currentTime - prefHelper.getPreviousAPICallTime(type);

        if(difference>10*60*1000){
            return true;
        }
        return false;
    }

    public void filterFragmentList(String str){};

    protected void saveAPICallTime(int type,long currentTime) {
        prefHelper.savePreviosAPICallTime(type,currentTime);
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }
    protected Dialog showProgressDialog(){
        dialog = UiDialog.getDialogFixed(mContext, R.layout.center_progress);
        // Glide.with(this).load(R.drawable.loader_small).placeholder(R.drawable.default_user).into(new GlideDrawableImageViewTarget((ImageView) dialog.findViewById(R.id.image_progress)));
        if(!getActivity().isFinishing())
            dialog.show();
        return dialog;
    }
    protected void dismissProgressDialog() {
        if (mContext != null && !((Activity) mContext).isFinishing() && dialog != null)
            dialog.dismiss();
    }

    public void filterList(String text) {
        filterFragmentList(text);
    }

    protected String getText(EditText etName) {
        return etName.getText().toString().trim();
    }

    public void toggleHeaderView(boolean b) {}

    /**
     * This will stop speaker if any
     */
    public void stopTextSpeaker(){
        if(tts!=null && tts.isSpeaking()){
            tts.stop();
            tts.shutdown();
        }
    }

    protected void speakText(final String message) {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    tts.setLanguage(Locale.getDefault());
                    tts.setPitch(1f);
                    tts.setSpeechRate(0.8f);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ttsGreater21(message,tts);
                    } else {
                        ttsUnder20(message,tts);
                    }
                }

            }
        });

    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text, TextToSpeech tts) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text, TextToSpeech tts) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    protected void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(containerId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    protected void addFragment(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(containerId, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    protected void replaceFragmentWithAnimation(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(containerId, fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    protected void addFragmentWithAnimation(int containerId, Fragment fragment, boolean addToBackStack){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.add(containerId, fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    protected void showToast(String s) {
        Toast.makeText(mContext,s, Toast.LENGTH_SHORT).show();
    }

    protected void updateProfileData(Map<String, String> map) {
        Call<ApiResponse> call= ApiClient.getClient().create(ApiInterface.class).updateProfile(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response!=null && response.body()!=null)
                {
                    if(response.body().getResponseCode()==200)
                    {
                        prefHelper.saveUserInfo(response.body().getUser());
                    }

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    protected DatePickerDialog openDatePicker(final Calendar calendar, final int identifier, final OnDateChoosenListener listener) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                listener.onDateChoosen(calendar, identifier);
            }

        };

       /* if(Build.VERSION.SDK_INT>23)
        {
            theme=android.R.style.TTheme_Holo_Light_Dialog_Alert;
        }
        else*/
            theme =  AlertDialog.THEME_DEVICE_DEFAULT_DARK;

        DatePickerDialog dialog = new DatePickerDialog(mContext,  theme, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();

        return dialog;
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

    protected void dialogConfirmationOk(String message, final Confirmation listener) {
        final Dialog dialog = UiDialog.getDialogFixed(mContext, R.layout.dialog_confirm);
        dialog.show();
        TextView tvOkay = (TextView) dialog.findViewById(R.id.tv_yes);
        tvOkay.setText("Ok");
        TextView tvNo = (TextView) dialog.findViewById(R.id.tv_no);
        tvNo.setVisibility(View.GONE);
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



}
