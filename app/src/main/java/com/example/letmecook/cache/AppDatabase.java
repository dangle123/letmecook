package com.example.letmecook.cache;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {RecipeEntity.class}, version = 1)
@TypeConverters(StringListConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DAO recipeDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "letmecook_offline.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
