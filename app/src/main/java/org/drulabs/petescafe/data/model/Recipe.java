package org.drulabs.petescafe.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.drulabs.petescafe.data.local.RecipeTypeConverter;

import java.util.List;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "servings")
    private int servings;

    @ColumnInfo(name = "image_url")
    private String image;

    @TypeConverters(RecipeTypeConverter.class)
    @ColumnInfo(name = "ingredient_list")
    private List<Ingredient> ingredients;

    @TypeConverters(RecipeTypeConverter.class)
    @ColumnInfo(name = "recipe_steps")
    private List<RecipeStep> steps;

    public Recipe(int id, String name, int servings, String image,
                  List<Ingredient> ingredients, List<RecipeStep> steps) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                '}';
    }
}
