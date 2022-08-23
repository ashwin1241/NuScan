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
public interface Queries {

    //Queries for the table Card_item

    @Delete
    void deleteItem(Card_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Card_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllItems(ArrayList<Card_item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Card_item item);

    @NonNull
    @Query("SELECT * FROM Card_item ORDER BY id ASC")
    public List<Card_item> getAllItems();

    @Query("DELETE FROM Card_item")
    void deleteAllItems();

    //Queries for the table Card_sub_item

    @Delete
    void deleteSubItem(Card_sub_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubItem(Card_sub_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSubItems(ArrayList<Card_sub_item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSubItem(Card_sub_item item);

    @NonNull
    @Query("SELECT * FROM Card_sub_item WHERE parent_id = :parentId ORDER BY id ASC")
    public List<Card_sub_item> getAllSubItems(long parentId);

    @Query("DELETE FROM Card_sub_item WHERE parent_id = :parentId")
    void deleteAllSpecificSubItems(long parentId);

    @Query("DELETE FROM Card_sub_item")
    void deleteAllSubItems();

}
