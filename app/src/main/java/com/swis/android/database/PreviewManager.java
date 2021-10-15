package com.swis.android.database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.swis.android.model.Preview;
import com.swis.android.util.AppConstants;

import java.util.ArrayList;

public class PreviewManager {
    public interface PreviewDataLoaded {
        void onDataLoaded(ArrayList<Preview> list);
    }
    public interface PreviewLoaded {
        void onDataLoaded(Preview list);
    }

    public static void insertData(final Context context, final Preview list)
    {
        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().previewDao().insert(list);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent pushNotification = new Intent(AppConstants.PREVIEW_COUNT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            }
        }.execute();


    }
    public static void insertData(final Context context, final ArrayList<Preview> list)
    {
        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().previewDao().insert(list);
                return null;
            }
        }.execute();
    }
    public static void updateData(final Context context, final Preview list)
    {

        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().previewDao().update(list);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent pushNotification = new Intent(AppConstants.PREVIEW_COUNT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            }
        }.execute();
    }
    public static void deleteData(final Context context, final Preview list)
    {

        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().previewDao().delete(list);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent pushNotification = new Intent(AppConstants.PREVIEW_COUNT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            }
        }.execute();
    }
    public static void deleteAllData(final Context context)
    {

        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().previewDao().deleteAll();
                return null;
            }
        }.execute();
    }
    public static void getAllData(final Context context, final PreviewDataLoaded clientDataLoaded)
    {
        new AsyncTask<Void,Void,ArrayList<Preview>>()
        {


            @Override
            protected ArrayList<Preview> doInBackground(Void... voids) {

                return (ArrayList<Preview>) DatabaseClient.getInstance(context).getAppDatabase().previewDao().getAll();
            }

            @Override
            protected void onPostExecute(ArrayList<Preview> result) {
                super.onPostExecute(result);
                clientDataLoaded.onDataLoaded(result);
            }
        }.execute();
    }
    public static void getPreviewData(final Context context, final PreviewLoaded clientDataLoaded,final long timestamp)
    {
        new AsyncTask<Void,Void,Preview>()
        {


            @Override
            protected Preview doInBackground(Void... voids) {

                return (Preview) DatabaseClient.getInstance(context).getAppDatabase().previewDao().getPreview(timestamp);
            }

            @Override
            protected void onPostExecute(Preview result) {
                super.onPostExecute(result);
                if(result!=null)
                clientDataLoaded.onDataLoaded(result);
            }
        }.execute();
    }
}
