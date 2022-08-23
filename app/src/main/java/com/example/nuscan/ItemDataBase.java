package com.example.nuscan;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;

@Database(entities={Card_item.class},version = 1)
public abstract class ItemDataBase extends RoomDatabase {

    private static ItemDataBase DB_INSTANCE;
    public static synchronized ItemDataBase getInstance(Context context)
    {
        if(DB_INSTANCE ==null)
        {
            DB_INSTANCE = Room.databaseBuilder(context, ItemDataBase.class,"ItemDataBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return DB_INSTANCE;
    }

    public abstract ItemQueriesDao itemQueries();

}
