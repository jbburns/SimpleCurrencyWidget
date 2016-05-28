package com.jbburns.simplecurrencywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Map;

/**
 * The configuration screen for the {@link MainWidget MainWidget} AppWidget.
 */
public class MainWidgetConfigureActivity extends Activity {
    private static final String PREFS_NAME = "com.jbburns.simplecurrencywidget.MainWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private Spinner rateProviderSpinner;
    private Spinner baseCurrencySpinner;
    private Spinner counterCurrencySpinner;
    private EditText baseAmountEditText;
    private EditText feePercentageEditText;
    private TextView widgetConfigurationHintTextView;
    private Switch buySellSwitch;
    private String buySell = "Buy";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MainWidgetConfigureActivity.this;

            String rateProvider = rateProviderSpinner.getSelectedItem().toString();
            String counterCurrency = counterCurrencySpinner.getSelectedItem().toString();
            String baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
            String baseAmount = "1";
            String feePercentage = "0.7";
            if (!baseAmountEditText.getText().toString().isEmpty()){
                baseAmount = baseAmountEditText.getText().toString();
            }
            if(!feePercentageEditText.getText().toString().isEmpty()) {
                feePercentage = feePercentageEditText.getText().toString();
            }

            // When the button is clicked, store the preferences locally
            savePreference(context, mAppWidgetId,"rateProvider",rateProvider);
            savePreference(context, mAppWidgetId, "counterCurrency", counterCurrency);
            savePreference(context, mAppWidgetId, "baseCurrency", baseCurrency);
            savePreference(context, mAppWidgetId, "baseAmount", baseAmount);
            savePreference(context, mAppWidgetId, "feePercentage", feePercentage);
            savePreference(context, mAppWidgetId, "buySell", buySell);

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

    static void savePreference(Context context,int appWidgetId, String key, String value){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_" + key, value);
        prefs.apply();
/*
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        Map<String,?> keys = sharedPreferences.getAll();

        System.out.println("Prefs:\n");
        for(Map.Entry<String,?> entry : keys.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }*/
    }

    static String loadStringPreference(Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String value = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_" + key, null);
        if (value != null) {
            return value;
        } else {
            return "Not Set";
        }
    }

    static void deletePreferences(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Map<String,?> keys = sharedPreferences.getAll();
       // System.out.println("Prefs to delete:\n");
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().contains(PREF_PREFIX_KEY + appWidgetId)) {
                //System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                prefs.remove(entry.getKey());
            }
        }
        prefs.apply();
    }

    private boolean isDouble (String input){
        boolean isDouble = true;
        try{
            Double.parseDouble(input);
        } catch (java.lang.NumberFormatException e){
            isDouble = false;
        }
        return isDouble;
    }

    private void validateInputs(){

        String rateProvider = rateProviderSpinner.getSelectedItem().toString();
        String counterCurrency = counterCurrencySpinner.getSelectedItem().toString();
        String baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
        String baseAmount = "1";
        String feePercentage = "0.7";
        if (!baseAmountEditText.getText().toString().isEmpty()){
            baseAmount = baseAmountEditText.getText().toString();
        }
        if(!feePercentageEditText.getText().toString().isEmpty()){
            feePercentage = feePercentageEditText.getText().toString();
        }
        Resources res = getResources();
        String widgetConfigurationHintText;
        if (buySellSwitch.isChecked()){
            widgetConfigurationHintText= String.format(res.getString(R.string.widgetConfigurationHintSellText),
                    baseCurrency, baseAmount, counterCurrency, feePercentage, counterCurrency);
        }
        else{
            widgetConfigurationHintText= String.format(res.getString(R.string.widgetConfigurationHintBuyText),
                    baseAmount, baseCurrency, counterCurrency, feePercentage, counterCurrency);
        }

        if(
                (!rateProvider.isEmpty())
                &&(!counterCurrency.isEmpty())
                &&(!baseCurrency.isEmpty())
                &&(!baseAmount.isEmpty())
                &&(!feePercentage.isEmpty())
                ){
            if (isDouble(baseAmount)){
                if ( Double.parseDouble(baseAmount) == Double.parseDouble("0") ) {
                    widgetConfigurationHintTextView.setText("Base amount cannot be zero");
                    widgetConfigurationHintTextView.setTextColor(Color.RED);
                    findViewById(R.id.add_button).setEnabled(false);
                    findViewById(R.id.add_button).setSaveEnabled(false);
                }
                else {
                    widgetConfigurationHintTextView.setText(widgetConfigurationHintText);
                    findViewById(R.id.add_button).setEnabled(true);
                    widgetConfigurationHintTextView.setTextColor(Color.BLACK);
                }
            }
            else {
                widgetConfigurationHintTextView.setText("Base amount unrecognized / not a valid decimal number");
                widgetConfigurationHintTextView.setTextColor(Color.RED);
                findViewById(R.id.add_button).setEnabled(false);
                findViewById(R.id.add_button).setSaveEnabled(false);
            }
        }
        else{
            widgetConfigurationHintTextView.setText("Selections required");
            widgetConfigurationHintTextView.setTextColor(Color.RED);
            findViewById(R.id.add_button).setEnabled(false);
            findViewById(R.id.add_button).setSaveEnabled(false);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        final String[] rateProvidersArray = getResources().getStringArray(R.array.rateProviders);

        setContentView(R.layout.main_widget_configure);

        rateProviderSpinner = (Spinner) findViewById(R.id.rateProviderSpinner);
        baseCurrencySpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);
        counterCurrencySpinner = (Spinner) findViewById(R.id.counterCurrencySpinner);

        baseAmountEditText = (EditText) findViewById(R.id.baseAmountEditText);
        feePercentageEditText = (EditText) findViewById(R.id.feePercentageEditText);
        widgetConfigurationHintTextView = (TextView) findViewById(R.id.widgetConfigurationHintTextView);

        buySellSwitch = (Switch) findViewById(R.id.buySellSwitch);

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

        buySellSwitch.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TextView baseAmountTextView = (TextView) findViewById(R.id.baseAmountTextView);
                    if(isChecked){
                        baseAmountTextView.setText(getResources().getString(R.string.baseAmountSellText));
                        buySell = "Sell";
                    }
                    else{
                        baseAmountTextView.setText(getResources().getString(R.string.baseAmountBuyText));
                        buySell = "Buy";
                    }
                    validateInputs();
                }
            }
        );

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
    }
}

