package ru.sergeev.yaksecast;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    TextView tvForecast1,tvForecast2,tvForecast3,tvForecast4,tvForecast5,tvForecast6,tvForecast7,tvForecast8,tvSunrise,tvSunset;
    ImageView imForecast1,imForecast2,imForecast3,imForecast4,imForecast5,imForecast6,imForecast7,imForecast8;
    TextView tvForecastTemp1,tvForecastTemp2,tvForecastTemp3,tvForecastTemp4,tvForecastTemp5,tvForecastTemp6,tvForecastTemp7,tvForecastTemp8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvTemp = findViewById(R.id.tvTemp);
        ivIcon = findViewById(R.id.ivIcon);
        tvDesk = findViewById(R.id.tvDesk);
        tvTime = findViewById(R.id.tvTime);
        api_key();
    }

    @Override
    protected void onResume() {
        super.onResume();
        api_key_main();
//        api_key();
    }

    private void api_key_main() {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=yakutsk&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
//                .url("https://api.openweathermap.org/data/2.5/forecast?q=Yakutsk&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
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

                @SuppressLint("SimpleDateFormat")
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONObject main = json.getJSONObject("main");
                        String temp = main.getString("temp");

                        JSONObject sys = json.getJSONObject("sys");
                        int sunrise = sys.getInt("sunrise");
                        String sunrise_text = new java.text.SimpleDateFormat("HH:mm").format(new Date(sunrise * 1000L));

                        int sunset = sys.getInt("sunset");
                        String sunset_text = new java.text.SimpleDateFormat("HH:mm").format(new Date(sunset * 1000L));

                        JSONArray main_weather = json.getJSONArray("weather");
                        JSONObject main_weather_icon = main_weather.getJSONObject(0);
                        String main_icon = main_weather_icon.getString("icon");
                        String main_deskription = main_weather_icon.getString("description");
                        main_deskription = main_deskription.substring(0, 1).toUpperCase() + main_deskription.substring(1);

                        dateTime = (int) json.get("dt");
                        @SuppressLint("SimpleDateFormat") String result = new java.text.SimpleDateFormat("HH:mm").format(new Date(dateTime * 1000L));


                        String finalMain_deskription = main_deskription;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tvDesk.setText(finalMain_deskription);
                                setImage(ivIcon, main_icon);
                                tvTemp.setText(temp+"℃");
                                tvTime.setText("Актуально на: " + result);
                                tvSunrise.setText(sunrise_text);
                                tvSunset.setText(sunset_text);
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
    private void api_key() {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .url("https://api.openweathermap.org/data/2.5/weather?q=yakutsk&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
                .url("https://api.openweathermap.org/data/2.5/forecast?q=Yakutsk&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
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

                @SuppressLint("SimpleDateFormat")
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                    try {
                        JSONObject json = new JSONObject(responseData);
//                        JSONArray jArray = json.getJSONArray("list");
//                        System.out.println("****JARRAY****" + jArray.length());
//
//                        JSONObject main_data = jArray.getJSONObject(0);
//
//                        JSONObject main = main_data.getJSONObject("main");
//                        String temp = main.getString("temp");
//
//
//                        JSONArray main_weather = main_data.getJSONArray("weather");
//                        JSONObject main_weather_icon = main_weather.getJSONObject(0);
//                        String main_icon = main_weather_icon.getString("icon");
//                        String main_deskription = main_weather_icon.getString("description");
//                        main_deskription = main_deskription.substring(0, 1).toUpperCase() + main_deskription.substring(1);
//
//                        dateTime = (int) main_data.get("dt");
//                        @SuppressLint("SimpleDateFormat") String result = new java.text.SimpleDateFormat("HH:mm").format(new Date(dateTime * 1000L));
//
//
//                        String finalMain_deskription = main_deskription;
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                tvDesk.setText(finalMain_deskription);
//                                setImage(ivIcon, main_icon);
//                                tvTemp.setText(temp+"℃");
//                                tvTime.setText("Актуально на: " + result);
//                            }
//                        });








//                        for (int a=1; a<6; a++)
//                        {
//                            JSONObject forecast_data = jArray.getJSONObject(a);
//                            JSONArray weather = forecast_data.getJSONArray("weather");
//                            JSONObject weather_object = weather.getJSONObject(0);
//                            String icon = weather_object.getString("icon");
//                        }

//                        for(int i=0; i<jArray.length(); i++){
//                            JSONObject json_data = jArray.getJSONObject(i);
//
//                            Log.d("log_tag", "test = " + i + " " + json_data.getString("dt_txt")
//                            );
//                        }

//                        JSONObject temp1 = json.getJSONObject("main");
//
//
//                        JSONArray weather = json.getJSONArray("weather");
//                        JSONObject object = weather.getJSONObject(0);
//
//                        dateTime = (int) json.get("dt");
//                        @SuppressLint("SimpleDateFormat") String result = new java.text.SimpleDateFormat("HH:mm").format(new Date(dateTime * 1000L));
//
//
//
//                        String description = object.getString("description");
//                        description = description.substring(0, 1).toUpperCase() + description.substring(1);
//
//                        String icon = object.getString("icon");
//                        double Temperature = temp1.getDouble("temp");
//                        String temp = String.valueOf(Temperature);
//
//                        String finalDescription = description;


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