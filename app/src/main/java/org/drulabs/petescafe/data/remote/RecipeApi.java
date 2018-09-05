package org.drulabs.petescafe.data.remote;

import org.drulabs.petescafe.data.model.Recipe;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface RecipeApi {

    String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";

    @GET("2017/May/59121517_baking/baking.json")
    Single<List<Recipe>> fetchRecipes();

}
