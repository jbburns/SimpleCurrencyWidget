package com.jbburns.simplecurrencywidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MainWidgetConfigureActivity MainWidgetConfigureActivity}
 */
public class MainWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

       // CharSequence widgetText = MainWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        String counterCurrency = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"counterCurrency");
        String rateProvider = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"rateProvider");
        String baseCurrency = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"baseCurrency");
        Float baseAmount  = MainWidgetConfigureActivity.loadFloatPreference(context, appWidgetId, "baseAmount");
        Float feePercentage  = MainWidgetConfigureActivity.loadFloatPreference(context, appWidgetId, "feePercentage");
        String baseCounterCurrencyText;
        if (baseAmount > 1){
            baseCounterCurrencyText = baseAmount.toString() + " " + baseCurrency + "/" + counterCurrency;
        }
        else {
            baseCounterCurrencyText = baseCurrency + "/" + counterCurrency;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.baseCounterCurrencyTextView,baseCounterCurrencyText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MainWidgetConfigureActivity.deletePreferences(context, appWidgetId);
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

