package com.swis.android.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.ViewDataBinding;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentChangeProfilePicBinding;
import com.swis.android.listeners.LoadFileListener;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;
import com.swis.android.util.ImageUtils;
import com.swis.android.util.NisinFile;
import com.swis.android.util.PermissionUtil;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.UiDialog;
import com.squareup.picasso.Picasso;
import com.swis.android.util.Util;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangeProfilePicFragment extends BaseFragment implements LoadFileListener, View.OnClickListener {

    private FragmentChangeProfilePicBinding binding;
    private LoadFileListener loadFileListener;
    private Uri capturedImageUri;
    private Uri resultUri;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_profile_pic;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {

        binding = (FragmentChangeProfilePicBinding) views;
        Glide.with(mContext).load(prefHelper.getUserInfo().getAvatar()).placeholder(R.drawable.default_user).into(binding.ivProfile);

        binding.ivProfile.setOnClickListener(this);
        binding.btnUpload.setOnClickListener(this);
        binding.tvUpload.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).showBackButton();
        ((HomeActivity) getActivity()).setTitle("");

    }

    @Override
    public void onStop() {
        super.onStop();
    }
        private void dialogConfirmationSelectImage() {
            final Dialog dialog = UiDialog.getBottomDialogFixed(mContext, R.layout.dialog_open_gallery_camera);
            dialog.show();
            TextView btnGallery = dialog.findViewById(R.id.btn_gallery);
            TextView btnCamera = dialog.findViewById(R.id.btn_camera);
            TextView btnCancel = dialog.findViewById(R.id.btn_cancel);

            btnGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PermissionUtil.checkSDCardPermission(mContext)) {
                        openGallery(ChangeProfilePicFragment.this, false);
                    } else
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.SDCARD_REQUEST_CODE);

                    dialog.dismiss();
                }
            });
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PermissionUtil.checkCameraPermission(mContext)) {
                        openCamera(ChangeProfilePicFragment.this, AppConstants.CAMERA_CAPTURE_REQUEST);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.CAMERA_REQUEST_CODE);                    }
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }




    private void openGallery(LoadFileListener fileListener, Boolean multi_selection) {
        loadFileListener = fileListener;
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= 18 && multi_selection)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstants.PICK_IMAGE_REQUEST);
    }

    protected void openCamera(LoadFileListener photoListener, int requestCode) {


        loadFileListener = photoListener;
        File file = createImageFile();
        if (file != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                capturedImageUri = Uri.fromFile(file);
            } else {
                capturedImageUri = FileProvider.getUriForFile(mContext, "com.swis.android", file);
            }
            Intent cameraIntent =new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
            startActivityForResult(cameraIntent, requestCode);
        }
    }

    private File createImageFile() {

        String imageFileName = "pro_pic";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            storageDir.mkdirs();
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
            if (image == null || !image.exists()) {
                image = new File(storageDir, imageFileName + ".jpg");
                if (image == null || !image.exists()) {
                    showToast("There is something error in creating file");
                }
            }
        } catch (Exception e) {
            showToast(e.getMessage());
        }
        return image;

    }

    @Override
    public void getFiles(List<NisinFile> arrImages, int sourceType) {
        try {
            CropImage.activity(Uri.fromFile(new File(arrImages.get(0).getPhotoPath()))).setAspectRatio(1,1).start(mContext, this);
        } catch (Exception e) {
            showToast("Something wrong!");
        }
    }

    @Override
    public void processingImages() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.CAMERA_REQUEST_CODE:
                if (PermissionUtil.checkCameraPermission(mContext)) {
                    openCamera(ChangeProfilePicFragment.this, AppConstants.CAMERA_CAPTURE_REQUEST);
                }
                else
                {
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    if (! showRationale) {

                        Util.showAlert(mContext, "To upload the profile picture, we need permission for camera and storage, Please allow from setting", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 200);

                            }
                        });



                    } else  {
                        Util.showAlert(mContext,"To upload the profile picture, we need permission for camera and storage");

                    }

                }
                break;

            case PermissionUtil.SDCARD_REQUEST_CODE:
                if (PermissionUtil.checkSDCardPermission(mContext)) {
                    openGallery(this, false);
                }
                else
                {
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (! showRationale) {

                        Util.showAlert(mContext, "To upload the profile picture, we need permission for storage, Please allow from setting", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 200);

                            }
                        });



                    } else  {
                        Util.showAlert(mContext,"To upload the profile picture, we need permission for storage");

                    }

                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                Glide.with(mContext).load(resultUri).into(binding.ivProfile);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }

        if (resultCode == Activity.RESULT_OK) {
            final ArrayList<NisinFile> arrList = new ArrayList<NisinFile>();
            switch (requestCode) {
                case 100:
                    if (capturedImageUri == null) {
                        if (data != null)
                            capturedImageUri = data.getData();
                    }
                    if (capturedImageUri == null) {
                        showToast("Please capture image in portrait mode only.");
                        return;
                    }
                    arrList.add(new NisinFile(ImageUtils.compressImage(mContext, capturedImageUri, null, "" + "2"), AppConstants.MediaType.IMAGE));
                    loadFileListener.getFiles(arrList, AppConstants.FileSourceType.CAMERA);
                    break;

                case 200:
                    if (data == null) {
                        showToast(AppConstants.SOMETHING_WENT_WRONG);
                        return;
                    }
                    if (data.getData() != null) {
                        arrList.add(new NisinFile(ImageUtils.compressImage(mContext, data.getData(), null, "" + prefHelper.getUserInfo().getId()), AppConstants.MediaType.IMAGE));
                        loadFileListener.getFiles(arrList, AppConstants.FileSourceType.GALLERY);
                    } else if (data.getClipData() != null) {
                        loadFileListener.processingImages();
                        ImageUtils.asyncCompressMultipleImage(mContext, data.getClipData(), "" + prefHelper.getUserInfo().getId(), new ImageUtils.OnCompressDone() {
                            @Override
                            public void onSuccess(List<String> uris) {
                                for (int i = 0; i < uris.size(); i++) {
                                    arrList.add(new NisinFile(uris.get(i), AppConstants.MediaType.IMAGE));
                                }
                                loadFileListener.getFiles(arrList, AppConstants.FileSourceType.GALLERY);
                            }
                        });

                    }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    private void updateProfilePic() {
        showProgressDialog();
        HashMap map=new HashMap<String, RequestBody>();
        File file=new File(resultUri.getPath());
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        map.put("avatar\"; filename=\"" + file.getName(), fileBody);

        Call<ApiResponse> service= ApiClient.getClient().create(ApiInterface.class).updateAvatar(map);
        service.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if(response!=null && response.body()!=null)
                {
                    if(response.body().getResponseCode() == 200)
                    {
                        prefHelper.saveUserInfo(response.body().getUser());
                        prefHelper.savePref(PrefsHelper.PRO_IMG_UPDATED, true);
                        ((HomeActivity)getActivity()).setUpBottomNavBarItem();
                        dialogConfirmationOk(response.body().getResponseMessage(),new Confirmation());

                    }
                    else
                    showToast(response.body().getResponseMessage());
                }
                else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                dismissProgressDialog();
                showToast(AppConstants.SERVER_ERROR);

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:
                dialogConfirmationSelectImage();
                break;
            case R.id.tv_upload:
                dialogConfirmationSelectImage();
                break;
            case R.id.btn_upload:
                if (resultUri != null && resultUri.getPath() != null) {
                    updateProfilePic();
                }
                break;

        }
    }
}




