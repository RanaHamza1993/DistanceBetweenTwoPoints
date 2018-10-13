package com.example.ranahamza.myCompany;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    PolylineOptions polylineOptions=null;

    MarkerOptions markerOptions;
    MarkerOptions meko;
    Marker marker;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    ArrayList<String> dist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listPoints = new ArrayList<>();
        dist=new ArrayList<>();
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();


                }
                listPoints.add(latLng);
                meko=new MarkerOptions();
                 markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if (listPoints.size() == 1) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(markerOptions);

                } else {
                   markerOptions= markerOptions.title("Distance").position(new LatLng(latLng.latitude,latLng.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                if(listPoints.size()==2)
                {
                    String url=getRequestUrl(listPoints.get(0),listPoints.get(1));


                    TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                    String duration=getRequestDuration(listPoints.get(0),listPoints.get(1));
                    TaskRequestDuration taskRequestDuration=new TaskRequestDuration();
                    taskRequestDuration.execute(duration);

                    //  String di=dist.get(0).toString();
                    //Toast.makeText(getApplicationContext(),di,Toast.LENGTH_LONG).show();
                    //marker.setSnippet(dist.get(0).toString());

                }

            }
        });
    }

    private String getRequestUrl(LatLng origin, LatLng destination) {

        String str_org=     "origin=" + origin.latitude+"," + origin.longitude;
        String str_dest=    "destination="+ destination.latitude+","+destination.longitude;
        String sensor=      "sensor=false";
        String mode=        "mode=driving";
        String param=       str_org +"&"+ str_dest +"&"+ sensor +"&"+ mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+ output + "?"+ param+"&key=AIzaSyAOlkF1GNNR26SYLaSIjEfnAkMgaXfqQjQ";
        return url;

    }
    private String getRequestDuration(LatLng origin, LatLng destination)
    {
        String str_org=     "origins=" + origin.latitude+"," + origin.longitude;
        String str_dest=    "destinations="+ destination.latitude+","+destination.longitude;
        String mode=        "mode=driving";
        String param=       str_org +"&"+ str_dest  +"&"+ mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/distancematrix/"+ output + "?units=metric&"+ param+"&key=AIzaSyA8zgDnew8po-LzIJL5kQl8xgTxk9k5o3I";
       // String url="https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=en&sensor=false&key=AIzaSyA8zgDnew8po-LzIJL5kQl8xgTxk9k5o3I";


        return url;

    }
    private String requestDirections(String reqUrl) throws IOException {
        String responseString="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{

            URL url=new URL(reqUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {

                stringBuffer.append(line);
            }
            responseString=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        finally {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }

    private String requestDuration(String reqUrl) throws IOException {
        String responseString="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{

            URL url=new URL(reqUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {

                stringBuffer.append(line);
            }
            responseString=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        finally {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;

        }
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            String responseString="";
            try{

                    responseString=requestDirections(strings[0]);

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >
    {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject=null;
            List<List<HashMap<String, String>>> routes=null;
            try{
                jsonObject=new JSONObject(strings[0]);
                DirectionsParser directionsParser=new DirectionsParser();
               routes= directionsParser.parse(jsonObject);
            }
            catch(JSONException e)
            {
                e.printStackTrace();

            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points=null;
            for(List<HashMap<String,String>>path:lists)
            {
                points=new ArrayList();
                polylineOptions=new PolylineOptions();
                for(HashMap<String,String> point:path)
                {
                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);


            }
            if(polylineOptions!=null)
            {

                mMap.addPolyline(polylineOptions);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Direction not found",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class TaskRequestDuration extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            String responseString="";
            String res="";
            try{

                responseString=requestDuration(strings[0]);

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonRespRouteDistance = new JSONObject(s);

                JSONObject result= jsonRespRouteDistance.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray ("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance");

                String distance = String.valueOf(result.getString("text"));

                mMap.addMarker(markerOptions.snippet(distance));

//                marker.setSnippet(distance);
              //  marker.showInfoWindow();
                dist.add(distance);
                Toast.makeText(getApplicationContext(),distance,Toast.LENGTH_LONG).show();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
