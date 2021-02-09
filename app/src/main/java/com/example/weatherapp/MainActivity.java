package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherInfoTextView;
    private static final String API_KEY = "apikey";

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            String result = "";
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong!", Toast.LENGTH_LONG).show();
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray array = new JSONArray(weatherInfo);
                for(int i=0; i< array.length();i++){
                    JSONObject jsonPart = array.getJSONObject(i);
                    String completeWeather = jsonPart.getString("main") + "-" + jsonPart.getString("description");
                    weatherInfoTextView.setText(completeWeather);
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(weatherInfoTextView.getWindowToken(), 0);

                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Something went wrong!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view){
        cityName = findViewById(R.id.city);
        DownloadTask downloadTask = new DownloadTask();
        try{
            String city = cityName.getText().toString();
            String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=" +  API_KEY;
            downloadTask.execute(url);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Something went wrong!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.city);
        weatherInfoTextView = findViewById(R.id.weatherInfo);
    }
}