package com.jbburns.simplecurrencywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

/**
 * The configuration screen for the {@link MainWidget MainWidget} AppWidget.
 */
public class MainWidgetConfigureActivity extends Activity {
    private static final String PREFS_NAME = "com.jbburns.simplecurrencywidget.MainWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private static String rateProvider;
    private static String counterCurrency;
    private static String baseCurrency;
    private static Double baseAmount = new Double("1");
    private static Double feePercentage = new Double("0.07");
    private static String widgetConfigurationHintText;

    private Spinner rateProviderSpinner;
    private Spinner baseCurrencySpinner;
    private Spinner counterCurrencySpinner;
    private EditText baseAmountEditText;
    private EditText feePercentageEditText;
    private TextView widgetConfigurationHintTextView;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
   // EditText mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MainWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            //String widgetText = mAppWidgetText.getText().toString();
            //saveTitlePref(context, mAppWidgetId, widgetText);
            savePreferences(context, mAppWidgetId);

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

    static void savePreferences(Context context,int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" + "rateProvider", rateProvider);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" + "counterCurrency", counterCurrency);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" +  "baseCurrency", baseCurrency);
        prefs.putFloat(PREF_PREFIX_KEY + appWidgetId + "_" +  "baseAmount", Float.parseFloat(baseAmount.toString()));
        prefs.putFloat(PREF_PREFIX_KEY + appWidgetId + "_" + "feePercentage", Float.parseFloat(feePercentage.toString()));
        prefs.apply();

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        Map<String,?> keys = sharedPreferences.getAll();

        System.out.println("Prefs:\n");
        for(Map.Entry<String,?> entry : keys.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    static String loadStringPreference(Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String value = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_" +  key, null);
        if (value != null) {
            return value;
        } else {
            return "Not Set";
        }
    }

    static Float loadFloatPreference(Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Float value = prefs.getFloat(PREF_PREFIX_KEY + appWidgetId + "_" + key, 0);
        if (value > 0) {
            return value;
        } else {
            return Float.parseFloat("0");
        }
    }

    static void deletePreferences(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Map<String,?> keys = sharedPreferences.getAll();
        System.out.println("Prefs to delete:\n");
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().contains(PREF_PREFIX_KEY + appWidgetId)) {
                System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                prefs.remove(entry.getKey());
            }
        }
        prefs.apply();
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

    private void validateInputs(){

        rateProvider = rateProviderSpinner.getSelectedItem().toString();
        counterCurrency = counterCurrencySpinner.getSelectedItem().toString();
        baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
        if (!baseAmountEditText.getText().toString().isEmpty()){
            baseAmount = Double.parseDouble(baseAmountEditText.getText().toString());
        }
        if(!feePercentageEditText.getText().toString().isEmpty()){
            feePercentage = Double.parseDouble(feePercentageEditText.getText().toString());
        }
        Resources res = getResources();
        //        <string name="widgetConfigurationHintText">"Rate will reflect a buy of %1$s %2$s with 1 %3$s, with a fee of %4$s4 %"</string>
        widgetConfigurationHintText = String.format(res.getString(R.string.widgetConfigurationHintText),
                baseAmount.toString(), baseCurrency, counterCurrency,feePercentage.toString() );

        if(
                (!rateProvider.isEmpty())
                &&(!counterCurrency.isEmpty())
                &&(!baseCurrency.isEmpty())
                &&(!baseAmount.isNaN())
                &&(!feePercentage.isNaN())
                ){
            widgetConfigurationHintTextView.setText(widgetConfigurationHintText);
            findViewById(R.id.add_button).setEnabled(true);
        }
        else{
            widgetConfigurationHintTextView.setText("Selections required");
            findViewById(R.id.add_button).setEnabled(false);
        }
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

       // final Spinner rateProviderSpinner = (Spinner) findViewById(R.id.rateProviderSpinner);
        rateProviderSpinner = (Spinner) findViewById(R.id.rateProviderSpinner);
        baseCurrencySpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);
        counterCurrencySpinner = (Spinner) findViewById(R.id.counterCurrencySpinner);

        baseAmountEditText = (EditText) findViewById(R.id.baseAmountEditText);
        feePercentageEditText = (EditText) findViewById(R.id.feePercentageEditText);
        widgetConfigurationHintTextView = (TextView) findViewById(R.id.widgetConfigurationHintTextView);

        ArrayAdapter<String> rateProvidersAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, rateProvidersArray);
        rateProviderSpinner.setAdapter(rateProvidersAdapter);

        rateProviderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String selectedrateProvider = rateProviderSpinner.getItemAtPosition(arg2).toString();
                String smbctbRateProviderText = getResources().getString(R.string.smbctbRateProviderText);

                if (selectedrateProvider.equals(smbctbRateProviderText)) {
                    final String[] smbctbDefaultBaseCurrenciesArray = getResources().getStringArray(R.array.smbctbDefaultBaseCurrencies);
                    final String[] smbctbDefaultCounterCurrenciesArray = getResources().getStringArray(R.array.smbctbDefaultCounterCurrencies);
                    ArrayAdapter<String> smbctbDefaultBaseCurrenciesAdapter = new ArrayAdapter<>(MainWidgetConfigureActivity.this, android.R.layout.simple_spinner_item, smbctbDefaultBaseCurrenciesArray);
                    baseCurrencySpinner.setAdapter(smbctbDefaultBaseCurrenciesAdapter);
                    ArrayAdapter<String> smbctbDefaultCounterCurrenciesAdapter = new ArrayAdapter<>(MainWidgetConfigureActivity.this, android.R.layout.simple_spinner_item, smbctbDefaultCounterCurrenciesArray);
                    counterCurrencySpinner.setAdapter(smbctbDefaultCounterCurrenciesAdapter);
                }

                validateInputs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        baseCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                validateInputs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        counterCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                validateInputs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        baseAmountEditText.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateInputs();
                return false;
            }
        });

        feePercentageEditText.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateInputs();
                return false;
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

