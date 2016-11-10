package tien.edu.hutech.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tien.edu.hutech.GPSTracking.GPSTracker;
import tien.edu.hutech.models.Store;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    public static final String EXTRA_STORE_LOCAL = "store_local";
    private static final int REQUEST_ID_PERMISSIONS = 201;

    GPSTracker gps;

    private GoogleMap mMap;

    private LatLng mSourceLatLng;
    private LatLng mDesLatLng;

    private ProgressDialog mProgressDialog;
    private Store mStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Intent intent = getIntent();

        mStore = (Store) intent.getSerializableExtra(EXTRA_STORE_LOCAL);
        String mLocalStore = mStore.getAddress();
        Log.d(TAG, mLocalStore);

        mDesLatLng = convertAddresstoLatLng(mLocalStore);

        Log.d(TAG, mDesLatLng.toString());

        showProgressDialog();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        hideProgressDialog();

        mMap.addMarker(new MarkerOptions().position(mDesLatLng).draggable(true).title(mStore.getName()));

    }

    private void getMyLocation(){
        gps = new GPSTracker(MapsActivity.this);
        if(gps.canGetLocation()){
            mSourceLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
            getDirection(mSourceLatLng, mDesLatLng);
        }
        else {
            gps.showSettingsAlert();
        }
    }

    public void getDirection(LatLng sourceLatLng, LatLng desLatLg) {

        String url = makeURL(sourceLatLng, desLatLg);

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void drawPath(String result) {

        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(mSourceLatLng, mDesLatLng);

        //Displaying the distance
        //Toast.makeText(this, String.valueOf(distance + " Meters"), Toast.LENGTH_SHORT).show();

        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(20)
                    .color(Color.BLUE)
                    .geodesic(true)
            );


        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    //Create URL request API Directrion
    public String makeURL(LatLng sourceLatLng, LatLng destLatLng) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourceLatLng.latitude + "");
        urlString.append(",");
        urlString
                .append(sourceLatLng.longitude + "");
        urlString.append("&destination=");// to
        urlString
                .append(destLatLng.latitude + "");
        urlString.append(",");
        urlString.append(destLatLng.longitude);
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&AIzaSyAmb5uKWANU9cp1KIwT5n_MA0gh-V3fASU");
        return urlString.toString();
    }

    //Convert from address to LatLng
    private LatLng convertAddresstoLatLng(String address) {
        LatLng mResult = null;

        Geocoder geocoder = new Geocoder(this);

        List<Address> mLstAddress = null;
        try {
            mLstAddress = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address mAddress = mLstAddress.get(0);
        mResult = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());

        return mResult;
    }

    private void askCheckPermissions() {
        int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (ACCESS_COARSE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED
                || ACCESS_FINE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED) {

            String[] lstPermission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MapsActivity.this, lstPermission, REQUEST_ID_PERMISSIONS);
        }
        else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_PERMISSIONS) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                getMyLocation();
            } else {
                Toast.makeText(this, R.string.static_Permissions_Denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveMaps(LatLng local) {
        mMap.addMarker(new MarkerOptions().position(local).draggable(true).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    protected void onResume() {
        askCheckPermissions();
        super.onResume();
    }

    /*private void moveToMyCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            mSourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            moveMaps(mSourceLatLng);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSourceLatLng, 18));
        }
    }*/

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
