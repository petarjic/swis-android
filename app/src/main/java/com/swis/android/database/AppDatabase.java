package com.swis.android.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.swis.android.model.Preview;

@Database(entities = {Preview.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PreviewDao previewDao();

}
