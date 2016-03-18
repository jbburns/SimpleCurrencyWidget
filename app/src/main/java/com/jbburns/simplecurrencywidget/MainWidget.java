package com.jbburns.simplecurrencywidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.res.Resources;
import android.widget.RemoteViews;

import org.apache.commons.lang.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MainWidgetConfigureActivity MainWidgetConfigureActivity}
 */
public class MainWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);

        String counterCurrency = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"counterCurrency");
        String rateProvider = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"rateProvider");
        String baseCurrency = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId,"baseCurrency");
        String baseAmount  = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId, "baseAmount");
        String feePercentage  = MainWidgetConfigureActivity.loadStringPreference(context, appWidgetId, "feePercentage");
        String baseCounterCurrencyText = baseCurrency + "/" + counterCurrency;
        if(NumberUtils.isNumber(baseAmount)){
            if (Integer.parseInt(baseAmount) > 1){
                baseCounterCurrencyText = baseAmount + " " + baseCurrency + "/" + counterCurrency;
            }
        }
        views.setTextViewText(R.id.baseCounterCurrencyTextView, baseCounterCurrencyText);
        String finalRateString;
        Date asOfTime = null;
        Double finalRate = null;
        Double rate = null;
        if(rateProvider.equals(context.getResources().getString(R.string.smbctbRateProviderText))){
            SMBCTBRate SMBCTBRate = null;
            SMBCTBRateProvider smbctbRateProvider = new SMBCTBRateProvider();
            SMBCTBRate = smbctbRateProvider.getRateByBaseCurrency(baseCurrency);
            rate = SMBCTBRate.getMidRate();
            asOfTime = SMBCTBRate.getAsOfTime();
        }

        if(rate != null){
            if (!NumberUtils.isNumber(feePercentage)){
                feePercentage = "0";
            }
            finalRate = (rate * (1+(Double.parseDouble(feePercentage)/100))) * Double.parseDouble(baseAmount);

            if (Double.parseDouble(feePercentage) > 0) {
                finalRateString = String.format("%.2f", finalRate) + "*";
            } else {
                finalRateString = String.format("%.2f", finalRate);
            }

            views.setTextViewText(R.id.rateDisplayTextView,finalRateString);
        }


        //Set asoftime
        if (asOfTime != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
            views.setTextViewText(R.id.rateAsOfTimeTextView, "As of: " + df.format(asOfTime));
        }

        //Set update time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
        String formattedDate = df.format(c.getTime());
        views.setTextViewText(R.id.widgetRefreshTimeTextView,formattedDate );

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

