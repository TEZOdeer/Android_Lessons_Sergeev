package ykt.tezodeer.sergeev.ru;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView view_city;
        TextView country_text;
        TextView temp_feels;
        TextView view_temp;
        TextView wind_deg;
        TextView speed_value;
        TextView view_desk;
        TextView humidity_text;

        ImageView image_city;
        ImageView weatherimage;
        EditText search_text;
        FloatingActionButton search_button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp_feels = (TextView) findViewById(R.id.temp_feels);
        humidity_text = (TextView) findViewById(R.id.humidity_text);
        country_text = (TextView) findViewById(R.id.country_text);
        wind_deg = (TextView) findViewById(R.id.wind_deg);
        speed_value = (TextView) findViewById(R.id.speed_value);
        view_city = (TextView) findViewById(R.id.town);
        view_temp = (TextView) findViewById(R.id.temp);
        view_desk = (TextView) findViewById(R.id.desk);
        weatherimage = (ImageView) findViewById(R.id.weatherimage);
        view_city.setText("");
        view_temp.setText("");
        speed_value.setText("");
        view_desk.setText("");
        search_text = (EditText) findViewById(R.id.search_text);
        search_button = (FloatingActionButton) findViewById(R.id.search_button);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getRootView().getWindowToken(), 0);
                api_key(String.valueOf(search_text.getText()));
            }

            private void api_key(final String City) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/weather?q="+City+"&appid=23df53519973b7a0f5b39b79e5b9aec4&units=metric&lang=ru")
                        .get()
                        .build();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    Response response = client.newCall(request).execute();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            setText(view_city, "Город не найден");
                            setText(view_desk, "");
                            setText(speed_value, "");
                            setText(wind_deg, "");
                            setText(country_text, "");
                            setText(view_temp, "");
                            setText(humidity_text, "");
                            setText(temp_feels, "");

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseData = response.body().string();
                            try {
                                JSONObject json = new JSONObject(responseData);
                                JSONArray array = json.getJSONArray("weather");
                                JSONObject object = array.getJSONObject(0);

                                String description = object.getString("description");
                                String icons = object.getString("icon");
                                JSONObject temp1 = json.getJSONObject("main");
                                double humidity0 = temp1.getDouble("humidity");
                                double Temperature = temp1.getDouble("temp");
                                double Temperature_feels = temp1.getDouble("feels_like");
                                JSONObject speed = json.getJSONObject("wind");
                                int wind_speed = speed.getInt("speed");
                                int wind_deg1 = speed.getInt("deg");
                                JSONObject sys = json.getJSONObject("sys");
                                String country = sys.getString("country");
                                String name0 = json.getString("name");
                                description = description.substring(0, 1).toUpperCase() + description.substring(1);


                                String feels_like0 = "Ощущается как: " + Temperature_feels + " °C";
                                setText(temp_feels, feels_like0);
                                String humidity1 = "Влажность: " + humidity0 + "%";
                                setText(humidity_text, humidity1);
                                String country_text0 = "Страна: " + country;
                                setText(country_text, country_text0);
                                String wind_deg0 = "Направление ветра (в градусах): " + wind_deg1;
                                String winds = "Скорость ветра: " + wind_speed + " м/с";
                                setText(wind_deg, wind_deg0);
                                setText(speed_value, winds);
                                setText(view_city, name0);
                                String temps = "Температура " + Math.round(Temperature) + " °C";
                                setText(view_temp, temps);
                                setText(view_desk, description);
                                setImage(weatherimage, icons);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                setText(view_city, "Город не найден");
                                setText(view_desk, "");
                                setText(speed_value, "");
                                setText(wind_deg, "");
                                setText(country_text, "");
                                setText(view_temp, "");
                                setText(humidity_text, "");
                                setText(temp_feels, "");
                            }
                        }

                        ;
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    setText(view_city, "Город не найден");
                    setText(view_desk, "");
                    setText(speed_value, "");
                    setText(wind_deg, "");
                    setText(country_text, "");
                    setText(view_temp, "");
                    setText(humidity_text, "");
                    setText(temp_feels, "");
                }
            }

            private void setText(final TextView text, final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(value);
                    }
                });
            }

            private void setImage(final ImageView imageView, final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (value) {
                            case "01d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon1));
                                break;
                            case "01n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon1));
                                break;
                            case "02d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon2));
                                break;
                            case "02n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon2));
                                break;
                            case "03d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon3));
                                break;
                            case "03n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon3));
                                break;
                            case "04d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                                break;
                            case "04n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                                break;
                            case "09d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                                break;
                            case "09n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                                break;
                            case "10d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                                break;
                            case "10n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                                break;
                            case "11d":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon7));
                                break;
                            case "11n":
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon7));
                                break;
                            default:
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather));
                        }
                    }
                });
            }
        });
    }
}