package com.example.rss_converter;

import android.content.Context;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrencyTaskLoader extends AsyncTaskLoader<List<Currency>> {

    private String param1, param2;

    public CurrencyTaskLoader(@NonNull Context context, String param1, String param2) {
        super(context);
        this.param1 = param1;
        this.param2 = param2;
    }

    @Nullable
    @Override
    public List<Currency> loadInBackground() {
        String address = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        Gson gson = new Gson();
        List<Currency> currencyList = new ArrayList<>();
        try{
            URL url = new URL(address);
            currencyList = gson.fromJson(
                    new JsonReader(new InputStreamReader(url.openStream())),
                    new TypeToken<ArrayList<Currency>>(){}.getType()
            );
        }catch(Exception e){
            e.printStackTrace();
        }

        currencyList.add(Currency.BASE);
        SystemClock.sleep(2000);
        return currencyList;
    }


}
