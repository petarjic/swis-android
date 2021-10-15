package com.swis.android.fragment


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.swis.android.ApiService.ApiClient
import com.swis.android.ApiService.ApiInterface

import com.swis.android.R
import com.swis.android.base.BaseFragment
import com.swis.android.databinding.FragmentProfileBinding
import com.swis.android.listeners.CustomTextChangeListener
import com.swis.android.listeners.LoadFileListener
import com.swis.android.model.requestmodel.ProfileUpdate
import com.swis.android.model.responsemodel.ApiResponse
import com.swis.android.model.responsemodel.UserRegisterResponseModel
import com.swis.android.util.*
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList


class ProfileFragment : BaseFragment(), LoadFileListener {



    var binding: FragmentProfileBinding? = null
    private lateinit var loadFileListener: LoadFileListener
    private lateinit var capturedImageUri: Uri
    var resultUri: Uri? =null



    override fun getLayoutId(): Int {
        return R.layout.fragment_profile

    }

    override fun onViewBinded(views: ViewDataBinding?) {
        binding = views as FragmentProfileBinding?


        if(!prefHelper.userInfo.name.isNullOrEmpty())
        binding!!.tvName.text=prefHelper.userInfo.name

        if(!prefHelper.userInfo.username.isNullOrEmpty())
            binding!!.tvUsername.text="@"+prefHelper.userInfo.username

        binding!!.ivProfile.setOnClickListener { dialogConfirmationSelectImage() }

        binding!!.btnContinue.setOnClickListener {
            if (!binding!!.etBio.text!!.isNullOrEmpty()) {
                if (resultUri != null && (!TextUtils.isEmpty(resultUri!!.path)))
                    updateProfilePic()
                else
                    updateProfileBio(binding!!.etBio.text.toString())
            }
            else
            {
                showToast("Please enter your bio")
            }
        }


        binding!!.etBio.addTextChangedListener(object : CustomTextChangeListener() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.toString().length>0)
                {
                    binding!!.tvMessage.visibility=View.GONE
                }
                else
                    binding!!.tvMessage.visibility=View.VISIBLE

            }
        })

    }


    private fun dialogConfirmationSelectImage() {
        val dialog = UiDialog.getBottomDialogFixed(mContext, R.layout.dialog_open_gallery_camera)
        dialog.show()
        val btnGallery = dialog.findViewById<TextView>(R.id.btn_gallery)
        val btnCamera = dialog.findViewById<TextView>(R.id.btn_camera)
        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)

        btnGallery.setOnClickListener(View.OnClickListener {
            if (PermissionUtil.checkSDCardPermission(mContext)) {
                openGallery(this, false)
            } else
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionUtil.SDCARD_REQUEST_CODE)


            dialog.dismiss()
        })
        btnCamera.setOnClickListener(View.OnClickListener {
            if (PermissionUtil.checkCameraPermission(mContext)) {
                openCamera(this, AppConstants.CAMERA_CAPTURE_REQUEST)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionUtil.CAMERA_REQUEST_CODE)
            }
            dialog.dismiss()
        })

        btnCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
    }


    fun openGallery(fileListener: LoadFileListener, multi_selection: Boolean) {
        loadFileListener = fileListener
        val intent = Intent()
        intent.type = "image/*"
        if (Build.VERSION.SDK_INT >= 18 && multi_selection)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstants.PICK_IMAGE_REQUEST)
    }

    private fun choosePhotoFromGallary() {

        /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/

        openGallery(this, false)
    }

    override fun getFiles(arrImages: MutableList<NisinFile>?, sourceType: Int) {
        try {
                 CropImage.activity(Uri.fromFile(File(arrImages?.get(0)?.photoPath))).start(mContext,this)
        } catch (e: Exception) {
            showToast("Something wrong!")
        }
    }

    override fun processingImages() {
    }

    protected fun openCamera(photoListener: LoadFileListener, requestCode: Int) {
        loadFileListener = photoListener
        val file = createImageFile()
        if (file != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                capturedImageUri = Uri.fromFile(file)
            } else {
                capturedImageUri = FileProvider.getUriForFile(mContext, "com.swis.android", file)
            }
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
            startActivityForResult(cameraIntent, requestCode)
        }
    }

    private fun createImageFile(): File? {
        val imageFileName = "profile_pic"
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        var image: File? = null
        try {
            storageDir.mkdirs()
            image = File.createTempFile(
                    imageFileName, // prefix
                    ".jpg", // suffix
                    storageDir      // directory
            )
            if (image == null || !image.exists()) {
                image = File(storageDir, "$imageFileName.jpg")
                if (image == null || !image.exists()) {
                    showToast("There is something error in creating file")
                }
            }
        } catch (e: Exception) {
            showToast(e.message)
        }

        return image
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        when (requestCode) {
            PermissionUtil.CAMERA_REQUEST_CODE -> if (PermissionUtil.checkCameraPermission(mContext)) {
                openCamera(this, AppConstants.CAMERA_CAPTURE_REQUEST)
            }
                else
                 {
                    val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    if (!showRationale) {

                            Util.showAlert(mContext, "Go to setting") {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", mContext.packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, 200)
                        }


                    } else {
                        Util.showAlert(mContext, "To upload the profile picture, we need permission for camera and storage")

                    }


                 }

            PermissionUtil.SDCARD_REQUEST_CODE -> if (PermissionUtil.checkSDCardPermission(mContext)) {
                choosePhotoFromGallary()
            }
            else
            {
                val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                if (!showRationale) {

                    Util.showAlert(mContext, "Go to setting") {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", mContext.packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 200)
                    }


                } else {
                    Util.showAlert(mContext, "To upload the profile picture, we need permission for storage")

                }


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                    resultUri = result.uri
                Glide.with(mContext).load(resultUri).into(binding!!.ivProfile)
                binding!!.tvUpload.visibility=View.GONE

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }

        }

        if (resultCode == Activity.RESULT_OK) {
            val arrList = ArrayList<NisinFile>()
            when (requestCode) {
                AppConstants.CAMERA_CAPTURE_REQUEST -> {
                    if (capturedImageUri == null) {
                        if (data != null)
                            capturedImageUri = data.data!!
                    }
                    if (capturedImageUri == null) {
                        showToast("Please capture image in portrait mode only.")
                        return
                    }
                    arrList.add(NisinFile(ImageUtils.compressImage(mContext, capturedImageUri, null, "" + "2"), AppConstants.MediaType.IMAGE))
                    loadFileListener.getFiles(arrList, AppConstants.FileSourceType.CAMERA)
                }
                AppConstants.PICK_IMAGE_REQUEST -> {
                    if (data == null) {
                        showToast(AppConstants.SOMETHING_WENT_WRONG)
                        return
                    }
                    if (data.data != null) {
                        arrList.add(NisinFile(ImageUtils.compressImage(mContext, data.data, null, "" + prefHelper.userInfo.id), AppConstants.MediaType.IMAGE))
                        loadFileListener.getFiles(arrList, AppConstants.FileSourceType.GALLERY)
                    } else if (data.clipData != null) {
                        loadFileListener.processingImages()
                        ImageUtils.asyncCompressMultipleImage(mContext, data.clipData, "" + prefHelper.userInfo.id, object : ImageUtils.OnCompressDone {
                            override fun onSuccess(uris: List<String>) {
                                for (i in uris.indices) {
                                    arrList.add(NisinFile(uris[i], AppConstants.MediaType.IMAGE))
                                }
                                loadFileListener.getFiles(arrList, AppConstants.FileSourceType.GALLERY)
                            }
                        })
                    }
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateProfilePic() {
        val map=HashMap<String,RequestBody>()
        val file=File(resultUri!!.path)
        val fileBody = RequestBody.create(MediaType.parse("image/*"), file)
        map.put("avatar\"; filename=\"" + file.getName(), fileBody);
        showProgressDialog()
        val service=ApiClient.getClient().create(ApiInterface::class.java).updateAvatar(map)
        service.enqueue(object : Callback<ApiResponse> {


            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                dismissProgressDialog()
                if(response!=null && response.body()!=null)
                {
                    if(response.body()!!.responseCode == 200)
                    {
                        Glide.with(mContext).load(Uri.parse(response.body()!!.user!!.avatar)).placeholder(R.drawable.default_user
                        ).error(R.drawable.default_user).into(binding!!.ivProfile)
                        binding!!.tvUpload.visibility=View.GONE
                        prefHelper.saveUserInfo(response.body()!!.user)
                        updateProfileBio(binding!!.etBio.text.toString())
                    }
                    else
                        showToast(response.body()!!.responseMessage)
                }
                else
                    showToast(AppConstants.SOMETHING_WENT_WRONG)
             }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                dismissProgressDialog()
                showToast(AppConstants.SERVER_ERROR)

            }
        })
    }

    private fun updateProfileBio(bio:String) {
            var data=ProfileUpdate()
        data.bio=bio
        showProgressDialog()
        var service=ApiClient.getClient().create(ApiInterface::class.java).updateProfile(data)
        service.enqueue(object : Callback<UserRegisterResponseModel> {


            override fun onResponse(call: Call<UserRegisterResponseModel>, response: Response<UserRegisterResponseModel>) {
                dismissProgressDialog()
                if(response!=null && response.body()!=null)
                {
                    if(response.body()!!.responseCode.equals("200",true))
                    {
                        prefHelper.saveUserInfo(response.body()!!.user)
                        prefHelper.savePref(PrefsHelper.IS_BIO_REGISTERED,true)
                        replaceFragment(R.id.main, FindFriendFragment(), true)
                    }
                    else
                        showToast(response.body()!!.responseMessage)
                }
                else
                    showToast(AppConstants.SOMETHING_WENT_WRONG)
            }

            override fun onFailure(call: Call<UserRegisterResponseModel>, t: Throwable) {
                dismissProgressDialog()
                showToast(AppConstants.SERVER_ERROR)

            }
        })
    }


}
