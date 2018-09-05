package org.drulabs.petescafe.data.local;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.drulabs.petescafe.data.model.Ingredient;
import org.drulabs.petescafe.data.model.RecipeStep;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RecipeTypeConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Ingredient> ingredientifyString(String ingredients) {
        if (ingredients == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();
        return gson.fromJson(ingredients, listType);
    }

    @TypeConverter
    public static String stringifyIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            return "";
        }
        return gson.toJson(ingredients);
    }

    @TypeConverter
    public static List<RecipeStep> stepifyString(String steps) {
        if (steps== null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<RecipeStep>>() {
        }.getType();
        return gson.fromJson(steps, listType);
    }

    @TypeConverter
    public static String stringifySteps(List<RecipeStep> steps) {
        if (steps == null) {
            return "";
        }
        return gson.toJson(steps);
    }

}
