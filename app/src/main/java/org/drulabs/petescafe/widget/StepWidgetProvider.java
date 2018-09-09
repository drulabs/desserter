package org.drulabs.petescafe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.di.AppComponent;
import org.drulabs.petescafe.ui.home.HomeScreen;

import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class StepWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.step_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        AppComponent appComponent = ((CafeApp) context.getApplicationContext())
                .getAppComponent();

        RecipeRepository recipeRepository = appComponent.getRecipeRepository();
        int recipeId = recipeRepository.getSavedRecipeId();
        String recipeName = recipeRepository.getSavedRecipeName();
        int stepId = recipeRepository.getRecipeStepId();

        String strRecipeDisplay = context.getString(R.string.step_recpice_display);

        Intent appLaunchIntent = new Intent(context, HomeScreen.class);

        if (recipeName != null) {
            appLaunchIntent.putExtra(HomeScreen.EXTRA_RECIPE_NAME, recipeName);
            appLaunchIntent.putExtra(HomeScreen.EXTRA_RECIPE_ID, recipeId);
            appLaunchIntent.putExtra(HomeScreen.EXTRA_STEP_ID, stepId);

            // id starts from 0, hence +1
            views.setTextViewText(R.id.step_recipe_name, String.format(Locale.getDefault(),
                    strRecipeDisplay, recipeName, (stepId + 1)));

            int cakeImageResId = WidgetUtils.getDrawableResId(recipeName);
            views.setImageViewResource(R.id.img_step_cake_resource, cakeImageResId);
        }

        PendingIntent continuePI = PendingIntent.getActivity(context,
                0, appLaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.rl_step_widget_holder, continuePI);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[]
            appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

