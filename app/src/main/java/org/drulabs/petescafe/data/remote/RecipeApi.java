package org.drulabs.petescafe.data.remote;

import org.drulabs.petescafe.data.model.Recipe;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface RecipeApi {

    String BASE_URL = "https://drulabs.github.io/";

    @GET("static/json/bakethecake.json")
    Single<List<Recipe>> fetchRecipes();

}
