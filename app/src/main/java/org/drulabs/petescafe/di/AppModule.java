package org.drulabs.petescafe.di;

import android.app.Application;
import android.content.Context;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.local.RecipeDAO;
import org.drulabs.petescafe.data.local.RecipeDB;
import org.drulabs.petescafe.data.remote.RecipeApi;
import org.drulabs.petescafe.data.remote.RecipeApiBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class AppModule {

    private Application application;

    public AppModule(Application app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public Application getApplication() {
        return application;
    }

    @Provides
    @Singleton
    public Context getApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public RecipeApi getRecipeApi() {
        return (new RecipeApiBuilder().build());
    }

    @Provides
    @Singleton
    RecipeDAO getRecipeDAO() {
        return RecipeDB.getINSTANCE(application).getRecipeDAO();
    }

    @Provides
    @Singleton
    RecipeRepository getRecipeRepository(Context context, RecipeApi recipeApi, RecipeDAO recipeDAO) {
        return new RecipeRepository(context, recipeApi, recipeDAO);
    }
}
