package org.drulabs.petescafe.di;

import android.app.Application;
import android.content.Context;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.local.RecipeDAO;
import org.drulabs.petescafe.data.remote.RecipeApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Application getApplication();

    Context getApplicationContext();

    RecipeApi getRecipeApi();

    RecipeDAO getRecipeDao();

    DetailComponent getDetailComponent(DetailsModule detailsModule);

}
