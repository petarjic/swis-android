package com.swis.android.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.swis.android.R;
import com.swis.android.model.ScreenSize;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Util {

    private static ProgressDialog dialog;
    private static int backCount = 0;
    private static Target target;
    private static PrefsHelper prefsHelper = PrefsHelper.getPrefsHelper();

    public static ProgressDialog showProgressDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait...");
        dialog.show();
        return dialog;
    }

    public static void dismissProgressDialog(Context context) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void showAlert(Context context, String responseMessage) {
        if (context != null && !((Activity) context).isFinishing()) {
            final Dialog dialog = UiDialog.getDialogFixed(context, R.layout.dialog_info);
            dialog.show();
            TextView tvYes = (TextView) dialog.findViewById(R.id.tv_yes);
            ((TextView) dialog.findViewById(R.id.tv_text)).setText(responseMessage != null ? responseMessage : "Something went wrong.");
            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    public static void showAlert(Context context, String responseMessage, DialogInterface.OnDismissListener listener) {
        if (context != null && !((Activity) context).isFinishing()) {
            final Dialog dialog = UiDialog.getDialogFixed(context, R.layout.dialog_info);
            dialog.show();
            TextView tvYes = (TextView) dialog.findViewById(R.id.tv_yes);
            ((TextView) dialog.findViewById(R.id.tv_text)).setText(responseMessage != null ? responseMessage : "Something went wrong.");
            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(listener);
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(viewBmp);

    }

    public static Bitmap getBitmapFromView(Context context, View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(context.getResources().getColor(R.color.colorPrimary));
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public static Bitmap takeScreenshotForView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }

    public static String getOptimisedUrl(String userWebsite) {
        if (userWebsite.contains("http")) {
            return userWebsite;
        } else {
            return "https://" + userWebsite;
        }
    }

    public static String round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return String.format("%.2f", bd.doubleValue());
    }

    public static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static String getVersionInfo(Context context) {
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionName = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    public static String getEmptyIfNull(String title) {
        return title != null ? title : "";
    }

    public static int safeConvertToIntFromString(String petrol) {
        return !TextUtils.isEmpty(petrol) && TextUtils.isDigitsOnly(petrol) ? Integer.parseInt(petrol) : 0;
    }

    public static float safeConvertToFloatFromString(String value) {
        return !TextUtils.isEmpty(value) ? Float.parseFloat(value) : 0;
    }

    public static void openPdf(Activity activity, String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + pdfUrl), "text/html");
        activity.startActivity(intent);
    }

    public static String getTimestampFromDateString(String dateStr, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = (Date) formatter.parse(dateStr);
            return String.valueOf(date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimestampFromDateStringUtc(String dateStr, String format) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formatter =
                new SimpleDateFormat(format, Locale.US);
        formatter.setTimeZone(timeZone);
        try {
            Date date = (Date) formatter.parse(dateStr);
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /* public static void openPhotoInZoom(Context context, String photoPath) {
         Picasso.with(context).invalidate(photoPath);
         PrefsHelper prefsHelper = PrefsHelper.getPrefsHelper();
         final Dialog dialog = UiDialog.getDialogFixed(context, R.layout.dialog_zoom_image);
         dialog.setCancelable(true);
         final TouchImageView imageView = (TouchImageView) dialog.findViewById(R.id.image_view);
         LinearLayout llOuter = (LinearLayout) dialog.findViewById(R.id.ll_outer);
         final Point size = prefsHelper.getScreenSize();
         LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
         params.width = size.x;
         params.height = size.y;
         imageView.setLayoutParams(params);
         target = new Target() {
             @Override
             public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                 imageView.setBitmap(bitmap, size);
                 imageView.setScaleType(ImageView.ScaleType.MATRIX);
             }

             @Override
             public void onBitmapFailed(Drawable errorDrawable) {

             }

             @Override
             public void onPrepareLoad(Drawable placeHolderDrawable) {

             }
         };
         if(URLUtil.isValidUrl(photoPath)){
             Picasso.with(context).load(photoPath).into(target);
         }else {
             Picasso.with(context).load(new File(photoPath)).into(target);
         }

         try {
             if (context != null && !((Activity) context).isFinishing())
                 dialog.show();
         } catch (Exception e) {
         }
     }
    */
    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public static void watchYoutubeVideo(Context context, String videoId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515*1.6*1000;
       // return Double.parseDouble((round(dist,2)));
        return Double.parseDouble(new DecimalFormat().format(dist));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public static String getTime(long miliseconds) {
        Calendar calendar = Calendar.getInstance();


        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);

        System.out.println("UTC:     " + simpleDateFormat.format(calendar.getTime()));
        long nowTime = calendar.getTimeInMillis();
        long different = nowTime - miliseconds;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = daysInMilli * 30;


        long elapsedMonth = different / monthInMilli;
        different = different % monthInMilli;

        long elapsedWeek = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedMonth > 0)
            return String.valueOf(elapsedMonth) + "mo";
        if (elapsedWeek > 0)
            return String.valueOf(elapsedWeek) + "w";
        if (elapsedDays > 0)
            return String.valueOf(elapsedDays) + "d";
        if (elapsedHours > 0)
            return String.valueOf(elapsedHours) + "h";
        if (elapsedMinutes > 0)
            return String.valueOf(elapsedMinutes) + "m";
        if (elapsedSeconds > 0)
            return String.valueOf(elapsedSeconds) + "s";

        return null;
    }

    public static void dialPhone(String mobile,Context mContext) {
        String toNumber = mobile; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + toNumber));
        mContext.startActivity(dialIntent);
    }

    public static void copyToClipboard(TextView textView,Context mContext) {
        ClipboardManager clipboardManager = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        CharSequence selectedTxt =  textView.getText().subSequence(textView.getSelectionStart(), textView.getSelectionEnd());
        ClipData clipData = ClipData.newPlainText("copied", selectedTxt);

        clipboardManager.setPrimaryClip(clipData);
    }

    public static ScreenSize getScreebSize(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        ScreenSize size = new ScreenSize(width, height);
        return size;
    }
}
