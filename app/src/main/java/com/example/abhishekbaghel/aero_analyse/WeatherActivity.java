package com.example.abhishekbaghel.aero_analyse;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends Activity {

    // static variables ----
    public static final String WX_URL = "https://forecast.weather.gov/MapClick.php?lat={lat}&lon={lon}&FcstType=json";
   // public static final String WX_URL = "http://forecast.weather.gov/MapClick.php?lat={lat}&lon={lon}&FcstType=json";

    // coords --- hard coded

    String latitude;
    String longitude;
    //String latitude = "35.913238";
    //String longitude = "-79.054663";

    // UI ----
    private TextView weatherDetailsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("long");
        setup();


    }


    private void setup() {
        // reference the TextView in the layout file
        weatherDetailsTxt = findViewById(R.id.weather_text);

        // inject the coords into the url ---
        String wxUrl = WX_URL.replace("{lat}", latitude);
        wxUrl = wxUrl.replace("{lon}", longitude);

        // start the task that will execute the JSON download
        GetDataTask tsk = new GetDataTask();
        tsk.execute(new String[]{wxUrl});
    }

    
    private void updateUI(String pTemp, String pConditions,String pPressure, String pWindSpeed,String pWindDirection, String pHumidity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Temperature: ").append(pTemp).append("degF\n")
                .append("Weather Condition: ").append(pConditions).append("\n")
                .append("Pressure: ").append(pPressure).append("in\n")
                .append("Wind Speed: ").append(pWindSpeed).append("mph\n")
                .append("Wind Direction: ").append(pWindDirection).append("deg(0 deg = North)\n")
                .append("Relative Humidity: ").append(pHumidity).append("%\n");

        weatherDetailsTxt.setText(sb.toString());
    }


    public void processWeatherData(String result) {
        // the raw JSON returned from the server
        System.out.println(result);
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject current = obj.getJSONObject("currentobservation");
            String temp = current.getString("Temp");
            String pressure = current.getString("SLP");
            String windSpeed = current.getString("Winds");
            String windDirection = current.getString("Windd");
            String humidity = current.getString("Relh");
            String weather = current.getString("Weather");

            updateUI(temp, weather, pressure, windSpeed, windDirection, humidity);
            //notifyUser(temp, weather);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    class GetDataTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            // set a response string --
            String response = "";

            // we got all kinds of try going on her that you need to seperate out in a real production app ---
            try {
                // create a url - params[0] = the url we passed in from the GetDataTask.execute()
                URL url = new URL(params[0]);
                // create a HttpUrlConnection for server communication and open the connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // connect to the endpoint
                connection.connect();
                // create a stream to load the bytes into
                InputStream in = connection.getInputStream();

                // BufferedReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                response = sb.toString();

            } catch(Exception e) {
                e.printStackTrace();
            }

            // send the response ----
            return response;
        }

        protected void onProgressUpdate(Integer... params) {

        }

        protected void onPostExecute(String result) {
            processWeatherData(result);
        }

    }
}
