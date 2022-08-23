package com.example.nuscan;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={Card_sub_item.class},version = 1)
public abstract class SubItemDataBase extends RoomDatabase {

    private static SubItemDataBase DB_INSTANCE;
    public static synchronized SubItemDataBase getInstance(Context context)
    {
        if(DB_INSTANCE ==null)
        {
            DB_INSTANCE = Room.databaseBuilder(context, SubItemDataBase.class,"SubItemDataBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return DB_INSTANCE;
    }

    public abstract SubItemQueriesDao subItemQueries();

}
