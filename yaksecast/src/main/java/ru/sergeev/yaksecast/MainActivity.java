package ru.sergeev.yaksecast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvTemp, tvDesk, tvTime;
    ImageView ivIcon;
    int dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date time = Calendar.getInstance().getTime();
        init();
    }

    private void init() {
        tvTemp = findViewById(R.id.tvTemp);
        ivIcon = findViewById(R.id.ivIcon);
        tvDesk = findViewById(R.id.tvDesk);
        tvTime = findViewById(R.id.tvTime);

        api_key();
    }

    private void api_key() {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=yakutsk&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                    try {
                        JSONObject json = new JSONObject(responseData);

                        JSONObject temp1 = json.getJSONObject("main");


                        JSONArray weather = json.getJSONArray("weather");
                        JSONObject object = weather.getJSONObject(0);

                        dateTime = (int) json.get("dt");
                        @SuppressLint("SimpleDateFormat") String result = new java.text.SimpleDateFormat("HH:mm").format(new Date(dateTime * 1000L));



                        String description = object.getString("description");
                        description = description.substring(0, 1).toUpperCase() + description.substring(1);

                        String icon = object.getString("icon");
                        double Temperature = temp1.getDouble("temp");
                        String temp = String.valueOf(Temperature);

                        String finalDescription = description;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                tvTemp.setText(temp+"℃");
                                setImage(ivIcon, icon);
                                tvDesk.setText(finalDescription);
                                tvTime.setText(result);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void setImage(final ImageView imageView, final String value) {
        runOnUiThread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                switch (value) {
                    case "01d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon01d));
                        break;
                    case "01n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon01n));
                        break;
                    case "02d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon02d));
                        break;
                    case "02n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon02n));
                        break;
                    case "03d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon03d));
                        break;
                    case "03n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon03n));
                        break;
                    case "04d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon04d));
                        break;
                    case "04n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon04n));
                        break;
                    case "09d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon09d));
                        break;
                    case "09n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon09n));
                        break;
                    case "10d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon10d));
                        break;
                    case "10n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon10n));
                        break;
                    case "11d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon11d));
                        break;
                    case "11n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon11n));
                        break;
                    case "13d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon13d));
                        break;
                    case "13n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon13n));
                        break;
                    case "50d":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon50d));
                        break;
                    case "50n":
                        ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon50n));
                        break;
//                    default:
//                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather));
                }
            }
        });
    }


}