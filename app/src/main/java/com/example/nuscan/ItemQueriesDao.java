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
public interface ItemQueriesDao {

    @Delete
    void deleteItem(Card_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Card_item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllItems(ArrayList<Card_item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Card_item item);

    @NonNull
    @Query("SELECT * FROM Card_item")
    public List<Card_item> getAllItems();

    @Query("DELETE FROM Card_item")
    void deleteAllItems();
}
