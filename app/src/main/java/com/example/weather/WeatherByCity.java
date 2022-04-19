package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class WeatherByCity extends AppCompatActivity {
    // Declare variables  globally

    TextView City, Description, Temperture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_by_city);


        //Assign value to each controller

        City = (TextView) findViewById(R.id.City);
        Description = (TextView) findViewById(R.id.Description);
        Temperture = (TextView) findViewById(R.id.Temperture);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String SearchedCity = intent.getStringExtra("City");

        // Check if the string empty and fetch the data
        if(SearchedCity != null){

            FetchData(SearchedCity);

        }
    }
    public void FetchData(String CityName) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+CityName+"&units=metric&appid=d2dbdfd42f50c9a4cc7fabcd32919ff5";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String DescriptionInfo = "";
                String CityInfo = "";
                double TempInfo = Double.NaN;

                //  Save the data in variable
                try{
                    JSONArray jsonArray = response.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    JSONObject jsonObjectMain = response.getJSONObject("main");
                    DescriptionInfo = jsonObjectWeather.getString("description");
                    TempInfo = jsonObjectMain.getDouble("temp");
                    CityInfo = response.getString("name");


                }catch(JSONException e){
                    e.printStackTrace();
                }

                // Update the Below TextViews with the fetched data
                City.setText(CityInfo);
                Description.setText(DescriptionInfo);
                Temperture.setText(String.valueOf((int)TempInfo)+ "\u00B0");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Response", "Didnt work");
            }
        });

// Add the request to the RequestQueue.
        queue.add(request);
    }
}