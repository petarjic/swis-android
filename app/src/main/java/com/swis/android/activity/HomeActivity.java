package com.swis.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivityHomeBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.fragment.FollowRequestFragment;
import com.swis.android.fragment.FollowersFragment;
import com.swis.android.fragment.HomeFragment;
import com.swis.android.fragment.LikesFragment;
import com.swis.android.fragment.ProfileNewFragment;
import com.swis.android.fragment.RecommendedFragment;
import com.swis.android.fragment.SettingFragment;
import com.swis.android.model.requestmodel.LocationUpdateRequestModel;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserRegisterResponseModel;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.PermissionUtil;
import com.swis.android.util.Util;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.ibrahimsn.lib.NiceBottomBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeActivity extends MiddleBaseActivity implements NiceBottomBar.BottomBarCallback {

    public ActivityHomeBinding binding;
    private boolean isSignup, isAddressUpdated;
    private final int REQUEST_CHECK_SETTINGS = 102;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private final long UPDATE_INTERVAL = (long) 10000;
    private final long FASTEST_INTERVAL = 2000L;
    private double latitude, longtitude;
    private int type, prevPos;
    private BottomNavigationMenuView mbottomNavigationMenuView;
    private String mainPostId,id,userName;
    private Fragment fragment;
    private boolean nav_to_profile;
    private boolean isFromNotiFlag;
    private Post commentPost;
    private boolean isfinish;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivityHomeBinding) views;

        if (bundle != null) {
            isSignup = bundle.getBoolean(AppConstants.IS_SIGN_UP);
            type = bundle.getInt(AppConstants.NOTIFICATIONS.TYPE);
            mainPostId = bundle.getString(AppConstants.NOTIFICATIONS.MAIN_POST_ID);
            id = bundle.getString(AppConstants.NOTIFICATIONS.ID);
            userName = bundle.getString(AppConstants.USER_NAME);
            nav_to_profile = bundle.getBoolean(AppConstants.NAVIGATE_TO_PROFILE);
            commentPost = bundle.getParcelable(AppConstants.EXTRA_COMMENT);
        }
        binding.navigation.setBottomBarCallback(this);
        float topMargin = (prefHelper.getScreenSize().getHeight()/100)*(int)(prefHelper.getScreenSize().getHeight()/750)+10;
        binding.navigation.setItemIconMargin(-topMargin);

       if(nav_to_profile)
       {
           navigateToConcernPage(type, commentPost);

       }
       else if (isSignup) {
            binding.navigation.setActiveItem(3);
            actionBarSetup();
            fragment=new RecommendedFragment();
            replaceFragment(R.id.frame_layout, fragment, false);

        } else {
            binding.navigation.setActiveItem(0);
            goToHomePage();

        }

        setUpBottomNavBarItem();
        prefHelper.savePref(prefHelper.IS_LOGIN,true);

        binding.appBar.drawerPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetting();
            }
        });

        setUpLocations();

        if (PermissionUtil.checkLocationPermission(mContext)) {
            getLastLocation();
            startLocationUpdates();
        } else {
            PermissionUtil.requestLocationPermission(mContext);
        }
        if (type > 0 && !nav_to_profile) {
            if (!TextUtils.isEmpty(mainPostId))
                fetchPostDetails(mainPostId, type);
            else if (!TextUtils.isEmpty(id))
                fetchPostDetails(id, type);
            else
                navigateToConcernPage(type, null);
        }
    }

    private void fetchPostDetails(String id, final int type) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchPostById(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {

                        navigateToConcernPage(type, response.body().getPost());

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

    }

    private void navigateToConcernPage(int type, Post post) {
        Bundle bundle;
        switch (type) {
            case 1:
                isFromNotiFlag=true;
                fragment = new LikesFragment();
                bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT, post);
                bundle.putString(AppConstants.EXTRA_DATA, (String.valueOf(getActionBarTitle())));
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(R.id.frame_layout, fragment, true);
                break;
            case 2:
                bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT,post);
                bundle.putString(AppConstants.EXTRA_ID, id);
                CommentActivity.start(mContext,bundle);
             break;
            case 3:
                isFromNotiFlag=true;
                fragment=new FollowRequestFragment();
                replaceFragmentWithAnimation(R.id.frame_layout,fragment, true);
                break;
            case 4:
                fragment = new FollowersFragment();
                bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT, post);
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(R.id.frame_layout, fragment, true);
                break;
            case 5:
                binding.navigation.setActiveItem(prefHelper.getPosition());
                fragment = new ProfileNewFragment();
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_ID, id);
                bundle.putString(AppConstants.USER_NAME, userName);
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(R.id.frame_layout, fragment, true);
                break;
        }
    }

    private void setUpLocations() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {

                    if (latitude == 0 && longtitude == 0) {
                        latitude = location.getLatitude();
                        longtitude = location.getLongitude();
                        isAddressUpdated = true;
                    }

                    Geocoder geocoder = null;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(mContext, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longtitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Util.distance(location.getLatitude(), location.getLongitude(), latitude, longtitude) >= 500.00 || isAddressUpdated) {


                        if ( addresses!=null && addresses.size() > 0) {
                            latitude = location.getLatitude();
                            longtitude = location.getLongitude();
                            LocationUpdateRequestModel locations = new LocationUpdateRequestModel();
                            // val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            locations.setLatitude(latitude);
                            locations.setLongitude(longtitude);
                            locations.setAddress(addresses.get(0).getAddressLine(0));

                            updateLocations(locations);
                            isAddressUpdated = false;
                        }
                    }
                }

                super.onLocationResult(locationResult);
            }
        };
    }

    public void setUpBottomNavBarItem() {

        Glide.with(mContext).load(prefHelper.getUserInfo().getAvatar()).asBitmap().placeholder(R.drawable.default_user)
                .into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                roundedBitmapDrawable.setCircular(true);
                roundedBitmapDrawable.setAntiAlias(true);
                binding.navigation.changeIcon(4, roundedBitmapDrawable);
            }



                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        setUpBottomNavBarItem();
                    }
                });
        ;
    }

    private void updateLocations(LocationUpdateRequestModel locations) {
        Call<UserRegisterResponseModel> service = ApiClient.getClient().create(ApiInterface.class).updateLocation(locations);
        service.enqueue(new Callback<UserRegisterResponseModel>() {
            @Override
            public void onResponse(Call<UserRegisterResponseModel> call, Response<UserRegisterResponseModel> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                        prefHelper.saveUserInfo(response.body().getUser());
                    } else
                        showToast(response.body().getResponseMessage());
                } else {
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
                }
            }

            @Override
            public void onFailure(Call<UserRegisterResponseModel> call, Throwable t) {
                showToast(AppConstants.SERVER_ERROR);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.LOCATION_REQUEST_CODE:
                if (PermissionUtil.checkLocationPermission(mContext)) {
                    getLastLocation();
                    startLocationUpdates();
                }
                break;

        }
    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }


    private void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);


        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }


    public void goToSetting() {
        fragment=new SettingFragment();
        replaceFragmentWithAnimation(R.id.frame_layout, fragment, true);
        binding.appBar.drawerPopUp.setVisibility(View.GONE);
       showBackButton();
    }


    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }


    public void actionBarSetup() {
        binding.appBar.drawerPopUp.setVisibility(View.VISIBLE);
        binding.appBar.actionBarHomeup.setVisibility(View.GONE);
    }

    public void showBackButton() {
        binding.appBar.actionBarHomeup.setVisibility(View.VISIBLE);
    }

    public void hideBackButton() {
        binding.appBar.actionBarHomeup.setVisibility(View.GONE);
    }


    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon, "");
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void setTitle(String text) {
        setActionBarTitle(text);
    }

    @Override
    public void onBackPressed() {
       if(getSupportFragmentManager().getBackStackEntryCount()==1 && nav_to_profile)
            finish();
       else
           super.onBackPressed();
    }



    public void hideOrShowBottomNavigationBar(boolean flag) {
        if (flag)
            binding.navigation.setVisibility(View.VISIBLE);
        else
            binding.navigation.setVisibility(View.GONE);
    }



    @Override
    public void onItemSelect(int pos) {

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        if(nav_to_profile)
        {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Intent pushNotification = new Intent(AppConstants.CLOSE_PREVIOUS_ACTIVITY);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
            nav_to_profile=false;

        }
        switch (pos) {

            case 0:
                prevPos = pos;
                prefHelper.savePosition(prevPos);
                goToHomePage();
                break;
            case 1:
                prevPos = pos;
                prefHelper.savePosition(prevPos);
                fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_DATA, "1");
                bundle.putString(AppConstants.EXTRA_TITLE, "Searches near me");
                fragment.setArguments(bundle);
                actionBarSetup();
                replaceFragment(R.id.frame_layout, fragment, false);
                break;
            case 2:
                binding.navigation.setActiveItem(prevPos);
                Calendar calendar = Calendar.getInstance();
                prefHelper.saveJourneyId(calendar.getTimeInMillis());
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean(AppConstants.IS_NEW_SEARCH, true);
                prefHelper.savePreviewInfo(null);
                Intent intent = new Intent(mContext, SearchActivity.class);
                intent.putExtras(bundle2);
                startActivityForResult(intent, AppConstants.RequestActivity.SEARCH_ACTIVITY);

                // SearchActivity.start(mContext, bundle2);
                break;
            case 3:
                prevPos = pos;
                prefHelper.savePosition(prevPos);
                actionBarSetup();
                fragment = new RecommendedFragment();
                replaceFragment(R.id.frame_layout, fragment, false);
                break;
            case 4:
                prevPos = pos;
                prefHelper.savePosition(prevPos);
                actionBarSetup();
                fragment = new ProfileNewFragment();
                replaceFragmentWithAnimation(R.id.frame_layout, fragment, false);
                break;
        }
    }

    public void goToHomePage() {
        fragment = new HomeFragment();
        Bundle bundle1 = new Bundle();
        fragment.setArguments(bundle1);
        actionBarSetup();
        replaceFragment(R.id.frame_layout, fragment, false);
    }

    @Override
    public void onItemReselect(int pos) {

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(nav_to_profile)
        {
            Intent pushNotification = new Intent(AppConstants.CLOSE_PREVIOUS_ACTIVITY);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

        }

        if(fragment instanceof SettingFragment || isFromNotiFlag || nav_to_profile) {
            nav_to_profile=false;
            switch (pos) {
                case 0:
                    isFromNotiFlag=false;
                    prevPos = pos;
                    prefHelper.savePosition(prevPos);
                    goToHomePage();
                    break;
                case 1:
                    prevPos = pos;
                    prefHelper.savePosition(prevPos);
                    fragment = new HomeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_DATA, "1");
                    bundle.putString(AppConstants.EXTRA_TITLE, "Searches near me");
                    fragment.setArguments(bundle);
                    actionBarSetup();
                    replaceFragment(R.id.frame_layout, fragment, false);
                    break;
                case 2:
                    binding.navigation.setActiveItem(prevPos);
                    Calendar calendar = Calendar.getInstance();
                    prefHelper.saveJourneyId(calendar.getTimeInMillis());
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean(AppConstants.IS_NEW_SEARCH, true);
                    prefHelper.savePreviewInfo(null);
                    Intent intent = new Intent(mContext, SearchActivity.class);
                    intent.putExtras(bundle2);
                    startActivityForResult(intent, AppConstants.RequestActivity.SEARCH_ACTIVITY);
                    break;
                case 3:
                    prevPos = pos;
                    prefHelper.savePosition(prevPos);
                    actionBarSetup();
                    fragment = new RecommendedFragment();
                    replaceFragment(R.id.frame_layout, fragment, false);
                    break;
                case 4:
                    prevPos = pos;
                    prefHelper.savePosition(prevPos);
                    actionBarSetup();
                    fragment = new ProfileNewFragment();
                    replaceFragmentWithAnimation(R.id.frame_layout, fragment, false);
                    break;
            }
        }
    }

    @Override
    public void onItemLongClick(int pos) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AppConstants.RequestActivity.SEARCH_ACTIVITY)
        {
            if(resultCode==RESULT_OK)
            {
                binding.navigation.setActiveItem(0);
                if(fragment instanceof HomeFragment){
                    Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                }else {
                    goToHomePage();
                }
            }
        }
    }
}
