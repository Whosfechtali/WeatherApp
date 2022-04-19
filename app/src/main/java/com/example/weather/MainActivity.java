package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements LocationListener {

    // Declare variables  globally
    LocationManager locationManager;
    String Latitude, Longitude;
    TextView City, Description, Temperture;
    Button SubmitButton;
    EditText SearchCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign value to each controller
        City = (TextView) findViewById(R.id.City);
        Description = (TextView) findViewById(R.id.Description);
        Temperture = (TextView) findViewById(R.id.Temperture);
        SubmitButton = (Button) findViewById(R.id.SubmitButton);
        SearchCity = (EditText) findViewById(R.id.SearchCity);

        // Check if the permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        // Call function to Ask for permission
        location();


        SubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Get the value of the input text
                    String EnteredCity = SearchCity.getText().toString();
                // Check if the Input text empty
                if(TextUtils.isEmpty(EnteredCity)) {
                // Show a Toast in case of the Input text is empty
                    Toast.makeText(MainActivity.this, "City name can not be empty",
                            Toast.LENGTH_LONG).show();
                 // In case of the input text   is not empty, start new activity
                }else{
                    Intent intent = new Intent(MainActivity.this, WeatherByCity.class);
                    intent.putExtra("City", EnteredCity);
                    startActivity(intent);
                }
            }
        });



    }

        // Function to ask for permission
    void location() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Getting the Latitude and Longitude of the user

    @Override
    public void onLocationChanged(Location location) {

        // Show the location in Text view

        Latitude = Double.toString(location.getLatitude());
        Longitude = Double.toString(location.getLongitude());
        FetchData(Latitude, Longitude);

    }

    // Function to Fetch the Data using Volley library
    public void FetchData(String lat, String lon) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units=metric&appid=d2dbdfd42f50c9a4cc7fabcd32919ff5";

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