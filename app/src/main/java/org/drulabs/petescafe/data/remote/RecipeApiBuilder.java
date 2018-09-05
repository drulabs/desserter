package org.drulabs.petescafe.data.remote;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeApiBuilder {

    private Retrofit.Builder recipeApiBuilder;

    public RecipeApiBuilder() {
        recipeApiBuilder = new Retrofit.Builder().baseUrl(RecipeApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public @NonNull RecipeApi build() {
        OkHttpClient okHttpClient = buildOkHttpClient();
        Retrofit retrofit = recipeApiBuilder.client(okHttpClient).build();
        return retrofit.create(RecipeApi.class);
    }

    private @NonNull OkHttpClient buildOkHttpClient() {
        // Logging and intercepting relating customizations
        // Not needed right now
        return new OkHttpClient();
    }
}
