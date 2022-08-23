package com.example.nuscan;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;

@Database(entities={Card_item.class,Card_sub_item.class},version = 1)
public abstract class DataBase extends RoomDatabase {

    private static DataBase DB_INSTANCE;
    public static synchronized DataBase getInstance(Context context)
    {
        if(DB_INSTANCE ==null)
        {
            DB_INSTANCE = Room.databaseBuilder(context, DataBase.class,"DataBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return DB_INSTANCE;
    }

    public abstract Queries itemQueries();

}
