package org.drulabs.petescafe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
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
public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_WIDGET_REFRESH = "org.drulabs.petescafe.widget.REFRESH";
    private static final int CODE_ACTION_REFRESH = 3;
    private static final int CODE_ACTION_START = 4;
    private static final int CODE_ACTION_CONTINUE = 5;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        try {
            CharSequence widgetText = context.getString(R.string.appwidget_text);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            views.setTextViewText(R.id.appwidget_text, widgetText);

            AppComponent appComponent = ((CafeApp) context.getApplicationContext())
                    .getAppComponent();

            RecipeRepository recipeRepository = appComponent.getRecipeRepository();
            int recipeId = recipeRepository.getSavedRecipeId();
            String recipeName = recipeRepository.getSavedRecipeName();
            int stepId = recipeRepository.getRecipeStepId();

            Intent widgetServiceIntent = new Intent(context, RecipeWidgetService.class);
            widgetServiceIntent.setData(Uri.parse(widgetServiceIntent.toUri(Intent
                    .URI_INTENT_SCHEME)));
            widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // launch app when empty widget is clicked
            Intent appOpenIntent = new Intent(context, HomeScreen.class);
            PendingIntent appOpenPI = PendingIntent.getActivity(context,
                    CODE_ACTION_START, appOpenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_text, appOpenPI);

            if (recipeName != null) {
                views.setTextViewText(R.id.remote_status,
                        String.format(Locale.getDefault(), context.getString(R.string
                                .txt_remote_status), (stepId + 1)));
                // id starts from 0, hence +1 above
                views.setTextViewText(R.id.remote_recipe_name, recipeName);

                // Intent to clear widget data
                Intent refreshIntent = new Intent(ACTION_WIDGET_REFRESH);
                PendingIntent refreshPI = PendingIntent.getBroadcast(context,
                        0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.remote_clear_status, refreshPI);

                // Intent to launch app with step displayed in the widget
                Intent continueIntent = new Intent(context, HomeScreen.class);
                continueIntent.putExtra(HomeScreen.EXTRA_RECIPE_NAME, recipeName);
                continueIntent.putExtra(HomeScreen.EXTRA_RECIPE_ID, recipeId);
                continueIntent.putExtra(HomeScreen.EXTRA_STEP_ID, stepId);
                PendingIntent continuePI = PendingIntent.getActivity(context,
                        CODE_ACTION_CONTINUE, continueIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.remote_status_continue, continuePI);

                // Update widget only when saved recipe is found
                views.setRemoteAdapter(R.id.remote_list_ingredient, widgetServiceIntent);
                views.setViewVisibility(R.id.rl_remote_widget_holder, View.VISIBLE);
                views.setViewVisibility(R.id.appwidget_text, View.GONE);
            } else {
                views.setViewVisibility(R.id.rl_remote_widget_holder, View.GONE);
                views.setViewVisibility(R.id.appwidget_text, View.VISIBLE);
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.remote_list_ingredient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds) {
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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null) {
            if (ACTION_WIDGET_REFRESH.equals(intent.getAction())) {
                WidgetUpdateService.startResetWidgetAction(context);
            }
        }
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

