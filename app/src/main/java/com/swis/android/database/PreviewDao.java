package com.swis.android.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.swis.android.model.Preview;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PreviewDao {
    @Query("SELECT * FROM Preview order by time desc")
    List<Preview> getAll();

    @Query("SELECT * FROM Preview where time =:timestamp ")
    Preview getPreview(long timestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Preview task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArrayList<Preview> task);

    @Delete
    void delete(Preview task);

    @Query("DELETE FROM preview")
    public void deleteAll();

    @Update
    void update(Preview task);
}
