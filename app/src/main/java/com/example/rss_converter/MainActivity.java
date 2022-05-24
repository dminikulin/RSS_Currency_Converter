package com.example.rss_converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.rss_converter.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Currency>>, Loader.OnLoadCanceledListener<List<Currency>> {

    //    private static final String LOG_TAG = "AndroidExample";
    private static final int LOADER_ID_CURRENCY = 10000;

    private static final String KEY_PARAM1 = "SomeKey1";
    private static final String KEY_PARAM2 = "SomeKey2";

//    private static final DecimalFormat df = new DecimalFormat("#.##");

    private LoaderManager loaderManager;

    private ActivityMainBinding binding;
    private String chosenCurrencyFrom, chosenCurrencyTo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fromCurrencyText.setOnClickListener(currencyMenuListener);
        binding.toCurrencyText.setOnClickListener(currencyMenuListener);
//        String amount_of_money = binding.moneyAmmount.getText().toString().trim();

        binding.convertButton.setOnClickListener(view -> {
            if (binding.moneyAmmount.getText().toString().matches("")) {
                Toast.makeText(MainActivity.this,
                        R.string.no_money_ammount,
                        Toast.LENGTH_LONG).show();
            } else if (chosenCurrencyTo == null && chosenCurrencyFrom == null) {
                Toast.makeText(MainActivity.this,
                        R.string.no_currency,
                        Toast.LENGTH_LONG).show();
            } else {
//                Log.e("FF", binding.moneyAmmount.getText().toString());
//                Log.e("FF", chosenCurrencyFrom + " " + chosenCurrencyTo);
                clickButtonConvert();
            }

        });

        binding.refreshLog.setText(getString(R.string.refreshed) + LocalDateTime.now());

        this.loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private final View.OnClickListener currencyMenuListener = new View.OnClickListener() {
        @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
        @Override
        public void onClick(View view) {
            PopupMenu currencyMenu;
            switch (view.getId()) {
                case R.id.fromCurrencyText:
                    currencyMenu = new PopupMenu(MainActivity.this, binding.fromCurrencyText);
                    currencyMenu.inflate(R.menu.currency_menu);
                    currencyMenu.setOnMenuItemClickListener(item -> {
                        chosenCurrencyFrom = (String) item.getTitle();
                        binding.fromCurrencyText.setText(getString(R.string.from_currency) + item);
                        return true;
                    });
                    currencyMenu.show();
                    break;
                case R.id.toCurrencyText:
                    currencyMenu = new PopupMenu(MainActivity.this, binding.toCurrencyText);
                    currencyMenu.inflate(R.menu.currency_menu);
                    currencyMenu.setOnMenuItemClickListener(item -> {
                        chosenCurrencyTo = (String) item.getTitle();
                        binding.toCurrencyText.setText(getString(R.string.to_currency) + item);
                        return true;
                    });
                    currencyMenu.show();
                    break;
            }
        }
    };

    private void clickButtonConvert() {
        LoaderManager.LoaderCallbacks<List<Currency>> loaderCallbacks = this;

        Bundle args = new Bundle();
        args.putString(KEY_PARAM1, "Some value 1");
        args.putString(KEY_PARAM2, "Some value 2");
//        Log.e("FF", args.toString());

        Loader<List<Currency>> loader = this.loaderManager.initLoader(LOADER_ID_CURRENCY, args, loaderCallbacks);
        try {
            loader.registerOnLoadCanceledListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loader.forceLoad();
    }

    private void clickButtonBack() {
        Loader<List<Currency>> loader = this.loaderManager.getLoader(LOADER_ID_CURRENCY);
        if (loader != null) {
            boolean cancelled = loader.cancelLoad();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refreshMenu) {
            binding.refreshLog.setText(getString(R.string.refreshed) + LocalDateTime.now());
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Currency>> onCreateLoader(int id, @Nullable Bundle args) {
        binding.resultText.setTextSize(24);
        binding.resultText.setText(R.string.loading);
        binding.convertButton.setEnabled(false);

        if (id == LOADER_ID_CURRENCY) {
            String param1 = (String) args.get(KEY_PARAM1);
            String param2 = (String) args.get(KEY_PARAM2);
            return new CurrencyTaskLoader(MainActivity.this, param1, param2);
        }
        throw new RuntimeException("onCreateLoader: something happened");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Currency>> loader, List<Currency> data) {
        int orientation = this.getResources().getConfiguration().orientation;

        if (loader.getId() == LOADER_ID_CURRENCY) {
            this.loaderManager.destroyLoader(loader.getId());

            Currency from_ccy = new Currency(), to_ccy = new Currency();
            double RESULT, money_entered = 0;
            String entered_value = binding.moneyAmmount.getText().toString();
            if(!"".equals(entered_value)){
                money_entered=Double.parseDouble(entered_value);
            }


            for (Currency ccy : data) {
                if (ccy.getCcy().equals(chosenCurrencyFrom)) {
                    from_ccy = ccy;
                }
                if (ccy.getCcy().equals(chosenCurrencyTo)) {
                    to_ccy = ccy;
                }
            }

//            Log.e("FF", String.valueOf(money_entered));
//            Log.e("FF", String.valueOf(from_ccy.getBuy()));
//            Log.e("FF", String.valueOf(to_ccy.getSale()));

            if (from_ccy.getBuy() > to_ccy.getBuy()) {
//                RESULT = Double.parseDouble(df.format(money_entered * from_ccy.getBuy()));
                RESULT = money_entered * Double.parseDouble(String.valueOf(from_ccy.getBuy()));
            } else {
                RESULT = money_entered * Double.parseDouble(String.valueOf(to_ccy.getSale()));
//                RESULT = Double.parseDouble(df.format(money_entered / to_ccy.getSale()));
            }

            String str;

            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                str = money_entered + " " + from_ccy.getCcy() + " = " + RESULT + " " + to_ccy.getCcy();
            } else{
                str = money_entered + " " + from_ccy.getCcy() + "\n = \n" + RESULT + " " + to_ccy.getCcy();
            }


            binding.resultText.setTextSize(36);
            binding.resultText.setText(str);
            binding.convertButton.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Currency>> loader) {
        binding.resultText.setText("");
    }

    @Override
    public void onLoadCanceled(@NonNull Loader<List<Currency>> loader) {
        if (loader.getId() == LOADER_ID_CURRENCY) {
            this.loaderManager.destroyLoader(loader.getId());
            binding.resultText.setText(R.string.load_failed);
            binding.convertButton.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        clickButtonBack();
    }
}