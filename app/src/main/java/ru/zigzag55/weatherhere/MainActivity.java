package ru.zigzag55.weatherhere;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final String api_key = Key.api_key;
    private final String api_call = "http://api.openweathermap.org/data/2.5/weather?q=CITY_NAME&units=metric&appid="; // CITY_NAME - replace and paste needed city
    private final String api_call_zip = "api.openweathermap.org/data/2.5/weather?zip={zip code},ru&appid="; // {zip code} - replace and paste needed code

    //TODO OnClickListener
    //TODO Zip-code search
    //TODO Wind direction + simplificate
    //TODO Invisible while not set the city
    //TODO add other Json data in the list (ListView?)

    private EditText editTextCity;
    private Button btnSetLocation;
    private TextView textViewDescription;
    private TextView textViewTemp;
    private TextView textViewWindDirection;
    private TextView textViewWindSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        btnSetLocation = findViewById(R.id.btnSetLocation);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewWindSpeed = findViewById(R.id.textViewWindSpeed);

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextCity.getText().toString().trim().isEmpty()) {
                    DownloadTask task = new DownloadTask();
                    try {
                        String res = task.execute(makeURL()).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String makeURL() {
        String cityName = editTextCity.getText().toString();
        String url = api_call.replace("CITY_NAME", cityName) + api_key + ("&lang=ru");
        return url;
    }

//    public String convertDegreeToCardinalDirection(int directionInDegrees){
//        String cardinalDirection = null;
//        if( (directionInDegrees >= 348.75) && (directionInDegrees <= 360) ||
//                (directionInDegrees >= 0) && (directionInDegrees <= 11.25)    ){
//            cardinalDirection = "N";
//        } else if( (directionInDegrees >= 11.25 ) && (directionInDegrees <= 33.75)){
//            cardinalDirection = "NNE";
//        } else if( (directionInDegrees >= 33.75 ) &&(directionInDegrees <= 56.25)){
//            cardinalDirection = "NE";
//        } else if( (directionInDegrees >= 56.25 ) && (directionInDegrees <= 78.75)){
//            cardinalDirection = "ENE";
//        } else if( (directionInDegrees >= 78.75 ) && (directionInDegrees <= 101.25) ){
//            cardinalDirection = "E";
//        } else if( (directionInDegrees >= 101.25) && (directionInDegrees <= 123.75) ){
//            cardinalDirection = "ESE";
//        } else if( (directionInDegrees >= 123.75) && (directionInDegrees <= 146.25) ){
//            cardinalDirection = "SE";
//        } else if( (directionInDegrees >= 146.25) && (directionInDegrees <= 168.75) ){
//            cardinalDirection = "SSE";
//        } else if( (directionInDegrees >= 168.75) && (directionInDegrees <= 191.25) ){
//            cardinalDirection = "S";
//        } else if( (directionInDegrees >= 191.25) && (directionInDegrees <= 213.75) ){
//            cardinalDirection = "SSW";
//        } else if( (directionInDegrees >= 213.75) && (directionInDegrees <= 236.25) ){
//            cardinalDirection = "SW";
//        } else if( (directionInDegrees >= 236.25) && (directionInDegrees <= 258.75) ){
//            cardinalDirection = "WSW";
//        } else if( (directionInDegrees >= 258.75) && (directionInDegrees <= 281.25) ){
//            cardinalDirection = "W";
//        } else if( (directionInDegrees >= 281.25) && (directionInDegrees <= 303.75) ){
//            cardinalDirection = "WNW";
//        } else if( (directionInDegrees >= 303.75) && (directionInDegrees <= 326.25) ){
//            cardinalDirection = "NW";
//        } else if( (directionInDegrees >= 326.25) && (directionInDegrees <= 348.75) ){
//            cardinalDirection = "NNW";
//        } else {
//            cardinalDirection = "?";
//        }
//
//        return cardinalDirection;
//    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = reader.readLine();
                while (line != null ){
                    result.append(line);
                    line = reader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject weather = jsonArray.getJSONObject(0);

                String cityName = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                String temperature = main.getString("temp");
                String tempFeelsLike = main.getString("feels_like"); // feels_like



                String windSpeed = jsonObject.getJSONObject("wind").getString("speed");

                String description = weather.getString("description");

                textViewDescription.setText(description);
                textViewTemp.setText(temperature);
//                textViewWindDirection.setText(temperature);
                textViewWindSpeed.setText(windSpeed);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}