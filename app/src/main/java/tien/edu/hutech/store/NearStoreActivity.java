package tien.edu.hutech.store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import tien.edu.hutech.GPSTracking.GPSTracker;
import tien.edu.hutech.models.Store;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.R;

public class NearStoreActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "NearStoreActivity";
    public static final String EXTRA_STORE_LOCAL = "store_local";
    private static final int REQUEST_ID_PERMISSIONS = 201;

    GPSTracker gps;

    private GoogleMap mMap;
    private LatLng mMyLocation;
    private ProgressDialog mProgressDialog;
    private ArrayList<Double> bankinh;


    private DatabaseReference mDatabase;
    private ArrayList<Store> stores = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private Circle mCircle;
    double r;
    private ProgressDialog progressDialog;
    private Marker mLastSelectedMarker;
    private int position;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)   {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_store);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        bankinh = new ArrayList<>();
        bankinh.add(500.0);
        bankinh.add(1000.0);
        bankinh.add(2000.0);
        bankinh.add(4000.0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Radius: ");

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_list_radius,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = new Spinner(getSupportActionBar().getThemedContext());
        spinner.setAdapter(spinnerAdapter);

        toolbar.addView(spinner, 0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        r = bankinh.get(0);
                        break;
                    case 1:
                        r = bankinh.get(1);
                        break;
                    case 2:
                        r = bankinh.get(2);
                        break;
                    case 3:
                        r = bankinh.get(3);
                        break;
                }
                if(mCircle == null){
                    drawMarkerWithCircle(mMyLocation, r);
                }
                else {
                    mCircle.setRadius(r);
                }
                for(Marker marker : markers){
                    double distance = SphericalUtil.computeDistanceBetween(mCircle.getCenter(),marker.getPosition());
                    if (distance < r) {
                        marker.setVisible(true);
                    }
                    else {
                        marker.setVisible(false);
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();

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

        getMyLocation();

        createMarker();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    private void getMyLocation(){
        gps = new GPSTracker(NearStoreActivity.this);
        if(gps.canGetLocation()){
            mMyLocation = new LatLng(gps.getLatitude(), gps.getLongitude());
            showMyLocation();
        }
        else {
            gps.showSettingsAlert();
        }
    }

    private void showMyLocation(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My Location");
        markerOptions.position(mMyLocation);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_location));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyLocation, 13));
    }

    private void createMarker() {
        mDatabase= FirebaseDatabase.getInstance().getReference("stores");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    Store store = userSnapshot.getValue(Store.class);

                    LatLng markerlatLng=new LatLng(store.getLat(), store.getLng());

                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerlatLng)
                            .title(store.getName())
                            .snippet(store.getAddress())
                            .visible(false));
                    markers.add(marker);
                    stores.add(store);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawMarkerWithCircle(LatLng position, Double radius) {
        double radiusInMeters = radius;
        int strokeColor =0xffff0000;
        int shadeColor=0x44ff0000;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = mMap.addCircle(circleOptions);
    }

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        mLastSelectedMarker = marker;
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(position != -1){
            Intent intent = new Intent(NearStoreActivity.this, DetailsActivity.class);
            intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, stores.get(position));
            startActivity(intent);
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;


        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }


        private void render(Marker marker, View view) {

            for(int i = 0; i < markers.size(); i++){
                if(marker.equals(markers.get(i))){
                    position = i;

                    break;
                }
                position = -1;
            }

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }
}
