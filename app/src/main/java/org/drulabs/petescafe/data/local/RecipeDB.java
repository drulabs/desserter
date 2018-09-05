package org.drulabs.petescafe.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import org.drulabs.petescafe.data.model.Ingredient;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.data.model.RecipeStep;

@Database(entities = {Recipe.class, RecipeStep.class, Ingredient.class}, version = 1,
        exportSchema = false)
public abstract class RecipeDB extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "recipes_db";
    private static RecipeDB INSTANCE;

    public static RecipeDB getINSTANCE(@NonNull Context context) {

        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, RecipeDB.class, DATABASE_NAME)
                            //.allowMainThreadQueries()
                            .build();
                }
            }

        }

        return INSTANCE;
    }

    public abstract RecipeDAO getRecipeDAO();
}
