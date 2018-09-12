package org.drulabs.petescafe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.ui.home.HomeScreen;

import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class StepWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_stackview);

        // Setting remote adapter
        Intent stepStackIntent = new Intent(context, StepWidgetService.class);
        stepStackIntent.putExtra(StepWidgetService.EXTRA_WIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.sv_step_stack, stepStackIntent);
        views.setEmptyView(R.id.sv_step_stack, R.id.tv_step_stack_msg);

        // Setting pending intent template for stackview
        Intent stackItemClickIntent = new Intent(context, HomeScreen.class);
        stackItemClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent stackItemPI = PendingIntent.getActivity(context,
                0, stackItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.sv_step_stack, stackItemPI);

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
        super.onUpdate(context, appWidgetManager, appWidgetIds);
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

