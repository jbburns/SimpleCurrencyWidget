package com.jbburns.simplecurrencywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * The configuration screen for the {@link MainWidget MainWidget} AppWidget.
 */
public class MainWidgetConfigureActivity extends Activity {
    private static final String PREFS_NAME = "com.jbburns.simplecurrencywidget.MainWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
   // EditText mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MainWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            //String widgetText = mAppWidgetText.getText().toString();
            //saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MainWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public MainWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

       // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String[] rateProvidersArray = getResources().getStringArray(R.array.rateProviders);

        setContentView(R.layout.main_widget_configure);

        final Spinner rateProviderSpinner = (Spinner) findViewById(R.id.rateProviderSpinner);
        ArrayAdapter<String> rateProvidersAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, rateProvidersArray);
        rateProviderSpinner.setAdapter(rateProvidersAdapter);

        rateProviderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Spinner baseCurrencySpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);
                Spinner counterCurrencySpinner = (Spinner) findViewById(R.id.counterCurrencySpinner);

                String selectedrateProvider = rateProviderSpinner.getItemAtPosition(arg2).toString();
                String smbctbRateProviderText = getResources().getString(R.string.smbctbRateProviderText);

               if (selectedrateProvider.equals(smbctbRateProviderText)){
                   final String[] smbctbDefaultBaseCurrenciesArray = getResources().getStringArray(R.array.smbctbDefaultBaseCurrencies);
                   final String[] smbctbDefaultCounterCurrenciesArray = getResources().getStringArray(R.array.smbctbDefaultCounterCurrencies);
                   ArrayAdapter<String> smbctbDefaultBaseCurrenciesAdapter = new ArrayAdapter<>(MainWidgetConfigureActivity.this,android.R.layout.simple_spinner_item, smbctbDefaultBaseCurrenciesArray);
                   baseCurrencySpinner.setAdapter(smbctbDefaultBaseCurrenciesAdapter);
                   ArrayAdapter<String> smbctbDefaultCounterCurrenciesAdapter = new ArrayAdapter<>(MainWidgetConfigureActivity.this,android.R.layout.simple_spinner_item, smbctbDefaultCounterCurrenciesArray);
                   counterCurrencySpinner.setAdapter(smbctbDefaultCounterCurrenciesAdapter);
               }

                  //list.add(s);
                  // listadapter.notifyDataSetChanged();
              }

              @Override
              public void onNothingSelected(AdapterView<?> arg0) {
                  // TODO Auto-generated method stub

              }
          });

        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadTitlePref(MainWidgetConfigureActivity.this, mAppWidgetId));
    }
}

