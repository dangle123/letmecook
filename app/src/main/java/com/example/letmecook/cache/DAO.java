package com.example.letmecook.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecipeEntity recipe);

    @Query("SELECT * FROM recipes_offline")
    List<RecipeEntity> getAllRecipes();

    @Query("SELECT * FROM recipes_offline WHERE id = :id LIMIT 1")
    RecipeEntity getRecipeById(String id);

    @Query("DELETE FROM recipes_offline WHERE id = :id")
    void deleteRecipeById(String id);

    @Delete
    void deleteRecipe(RecipeEntity recipe);
}
