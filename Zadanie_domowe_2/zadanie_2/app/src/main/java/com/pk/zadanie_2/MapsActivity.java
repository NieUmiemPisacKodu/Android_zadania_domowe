package com.pk.zadanie_2;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener, SensorEventListener {


    private static final String DATA_JSON_FILE = "data.json" ;
    List<MyMarker> markerList;

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 101;
    //private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    Marker gpsMarker = null;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private TextView sensorDisplay;
    private FloatingActionButton Fab1;
    private FloatingActionButton Fab2;
    private Button clearButton;
    private boolean isSensorWorking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        markerList = new ArrayList<>();
        sensorDisplay = findViewById(R.id.sensorDisplay);
        clearButton = findViewById(R.id.clear);
        Fab1 = findViewById(R.id.floatingActionButton);
        Fab2 = findViewById(R.id.floatingActionButton2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        isSensorWorking = false;
        Fab1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                sensorDisplay.setVisibility(View.VISIBLE);
                isSensorWorking = !isSensorWorking;
                startSensor(isSensorWorking);
            }
        });
        Fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fab1.setVisibility(View.INVISIBLE);
                Fab2.setVisibility(View.INVISIBLE);
                Fab1.animate().translationY(120f).alpha(0f).setDuration(1000);
                Fab2.animate().translationY(120f).alpha(0f).setDuration(1000);
                sensorDisplay.setVisibility(View.INVISIBLE);

                startSensor(false);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                markerList.removeAll(markerList);
                //SaveToJson();
            }
        });
    }
    public void zoomInClick(View v){
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    public void zoomOutClick(View v){
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);

        restoreFromJson();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(bitmapDescriptorFromVector(this, R.drawable.marker))
                .alpha(0.8f)
                .title(String.format("Position: (%.2f, %.2f)", latLng.latitude, latLng.longitude)));

        markerList.add(new MyMarker(latLng.latitude, latLng.longitude));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(20, 10, vectorDrawable.getIntrinsicWidth() , vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        Fab1.animate().translationY(0f).alpha(1f).setDuration(1000);
        Fab2.animate().translationY(0f).alpha(1f).setDuration(1000);
        return false;
    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback ,null);
    }

    private void createLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    if (gpsMarker != null)
                        gpsMarker.remove();
                }
            }
        };
    }

    @Override
    public void onMapLoaded()
    {
        Log.i(MapsActivity.class.getSimpleName(), "MapLoaded");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
    }

    @Override
    protected  void  onPause(){
        super.onPause();
        stopLocationUpdates();

        if(mSensor != null)
            sensorManager.unregisterListener(this);
    }

    private void stopLocationUpdates()
    {
        if(locationCallback != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    void startSensor(boolean isSensorWorking)
    {
        if(mSensor != null){

            if(isSensorWorking) {
                sensorDisplay.setVisibility(View.VISIBLE);
                sensorManager.registerListener( this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }else {
                sensorDisplay.setVisibility(View.INVISIBLE);
                sensorManager.unregisterListener(this);
            }
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorDisplay.setText("Accelerator: X: "+event.values[0] +" Y: " + event.values[1]);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void SaveToJson() {
        Gson gson = new Gson();
        String listJson = gson.toJson(markerList);
        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(DATA_JSON_FILE,MODE_PRIVATE);
            FileWriter writer = new FileWriter(outputStream.getFD());
            writer.write(listJson);
            writer.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void restoreFromJson() {
        FileInputStream inputStream;
        int BUFFER = 10000;
        Gson gson = new Gson();
        String readJson;

        try {
            inputStream = openFileInput(DATA_JSON_FILE);
            FileReader reader = new FileReader(inputStream.getFD());
            int n;
            char[] buf = new char[BUFFER];
            StringBuilder builder = new StringBuilder();
            while((n = reader.read(buf)) >= 0){
                String tmp = String.valueOf(buf);
                String substring = (n<BUFFER) ? tmp.substring(0,n) : tmp;
                builder.append(substring);
            }
            reader.close();
            readJson = builder.toString();
            Type collectionType = new TypeToken<List<MyMarker>>() { }.getType();
            List<MyMarker> o = gson.fromJson(readJson, collectionType);

            if(o != null){
                for(MyMarker mc : o) {
                    markerList.add(mc);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mc.mlatitude, mc.mlongitude))
                            .icon(bitmapDescriptorFromVector(this, R.drawable.marker))
                            .alpha(0.8f)
                            .title(String.format("Position: (%.2f, %.2f)", mc.mlatitude, mc.mlongitude)));
                }
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        SaveToJson();
        super.onDestroy();
    }

    private class Gson {
        public String toJson(List<MyMarker> markerList) {
            return null;
        }
        public List<MyMarker> fromJson(String readJson, Type collectionType) {
            return null;
        }
    }

    private class TypeToken<T> {
        public Type getType() {
            return null;
        }
    }
}