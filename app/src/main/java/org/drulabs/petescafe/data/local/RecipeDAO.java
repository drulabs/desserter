package org.drulabs.petescafe.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.drulabs.petescafe.data.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM recipes")
    LiveData<List<Recipe>> getRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<Recipe> getRecipe(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addRecipes(List<Recipe> recipes);

    @Query("DELETE FROM recipes")
    void clearAll();
}
