package com.example.nuscan;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SubItemQueriesDao {

    @Delete
    void deleteSubItem(Card_sub_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubItem(Card_sub_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSubItems(ArrayList<Card_sub_item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSubItem(Card_sub_item item);

    @NonNull
    @Query("SELECT * FROM Card_sub_item WHERE parent_id = :parentId")
    public List<Card_sub_item> getAllSubItems(long parentId);

    @Query("DELETE FROM Card_sub_item WHERE parent_id = :parentId")
    void deleteAllSpecificSubItems(long parentId);

    @Query("DELETE FROM Card_sub_item")
    void deleteAllSubItems();
}
