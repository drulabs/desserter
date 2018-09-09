package org.drulabs.petescafe.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.drulabs.petescafe.app.CafeApp;
import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.di.AppComponent;

/**
 * An {@link IntentService} subclass for updating widgets in on a separate handler thread.
 */
public class WidgetUpdateService extends IntentService {
    private static final String ACTION_UPDATE_WIDGETS = "org.drulabs.petescafe.widget.action" +
            ".UPDATE_WIDGETS";
    private static final String ACTION_RESET_WIDGETS = "org.drulabs.petescafe.widget.action" +
            ".RESET_WIDGETS";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    /**
     * Starts this service to update widget.
     *
     * @see IntentService
     */
    public static void startWidgetUpdate(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    /**
     * Starts this service to reset widgets.
     *
     * @see IntentService
     */
    public static void startResetWidgetAction(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_RESET_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleWidgetUpdate();
            } else if (ACTION_RESET_WIDGETS.equals(intent.getAction())) {
                handleWidgetReset();
            }
        }
    }

    /**
     * Handle action update reset in the provided background thread.
     */
    private void handleWidgetReset() {
        AppComponent appComponent = ((CafeApp) getApplicationContext())
                .getAppComponent();
        RecipeRepository recipeRepository = appComponent.getRecipeRepository();

        // clear the widget data
        recipeRepository.clearWidgetData();

        // update the widgets
        handleWidgetUpdate();

    }

    /**
     * Handle action update widget in the provided background thread.
     */
    private void handleWidgetUpdate() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(this,
                RecipeWidgetProvider.class));
        RecipeWidgetProvider.updateAppWidgets(getApplicationContext(), widgetManager, widgetIds);

        int[] stepWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(this,
                StepWidgetProvider.class));
        StepWidgetProvider.updateAppWidgets(getApplicationContext(), widgetManager, stepWidgetIds);

    }
}
