package com.example.letmecook.cache;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "recipes_offline")
public class RecipeEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    public String imageUrl;
    public String cookingTime;
    public String categoryId;
    public int coin;
    public double rating;
    public int like;
    public int view;

    @TypeConverters(StringListConverter.class)
    public List<String> ingredients;

    @TypeConverters(StringListConverter.class)
    public List<String> instructions;

    @TypeConverters(StringListConverter.class)
    public List<String> tips;
}
