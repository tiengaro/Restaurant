package tien.edu.hutech.restaurant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import tien.edu.hutech.models.Menu;
import tien.edu.hutech.models.Store;
import tien.edu.hutech.viewholder.MenuViewHolder;

public class DetailsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "DetailsActivity";
    public static final String EXTRA_STORE_KEY = "store_key";
    public static final int MY_PERMISSION_REQUEST_CALL_PHONE = 101;
    //Add map
    private GoogleMap mMap;

    //Defind toolbar;
    private Toolbar toolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    //Define Firebase
    private DatabaseReference mStoreReference;
    private DatabaseReference mMenuReference;
    ValueEventListener mStoreListener;
    private String mstoreKey;

    Store store = new Store();
    private LatLng mLatLngStore;

    //Define views
    private View        layoutCall;
    private ImageView   imgDetailImage;
    private ImageView   imgDetailFavorite;
    private View        layoutIconDirections;
    private View        layoutIconCall;
    private TextView    txtDetailStore;
    private TextView    txtDetailAddress;
    private TextView    txtDetailOpen;
    private TextView    txtDetailPhone;

    //Define recyclerview and adapter
    private FirebaseRecyclerAdapter<Menu, MenuViewHolder> mAdapter;
    private RecyclerView rcvMenu;
    private GridLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Config Appbar
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        mCollapsingToolbarLayout.setTitle("Restaurant");
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Config Actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Initialize views
        layoutCall              = findViewById(R.id.layoutCall);
        imgDetailImage          = (ImageView) findViewById(R.id.imgDetailImage);
        imgDetailFavorite       = (ImageView) findViewById(R.id.imgDetailFavorite);
        layoutIconDirections    = (View) findViewById(R.id.layoutIconDirections);
        layoutIconCall          = (View) findViewById(R.id.layoutIconCall);
        txtDetailStore          = (TextView) findViewById(R.id.txtDetailStore);
        txtDetailAddress        = (TextView) findViewById(R.id.txtDetailAddress);
        txtDetailOpen           = (TextView) findViewById(R.id.txtDetailOpen);
        txtDetailPhone          = (TextView) findViewById(R.id.txtDetailPhone);

        //Get store key from intent
        mstoreKey = getIntent().getStringExtra(EXTRA_STORE_KEY);

        //Initialize Database for a store
        mStoreReference = FirebaseDatabase.getInstance().getReference()
                .child("stores").child(mstoreKey);

        mMenuReference = FirebaseDatabase.getInstance().getReference()
                .child("menu");
        mManager = new GridLayoutManager(DetailsActivity.this, 2);

        final Query mMenuQuery = mMenuReference.child(mstoreKey).limitToFirst(10);

        //Initialize recyclerview
        rcvMenu = (RecyclerView) findViewById(R.id.rcvMenu);
        rcvMenu.setHasFixedSize(true);
        rcvMenu.setLayoutManager(mManager);

        mAdapter = new FirebaseRecyclerAdapter<Menu, MenuViewHolder>(
                Menu.class,
                R.layout.itemmenu,
                MenuViewHolder.class,
                mMenuQuery)
        {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Menu model, int position) {
                Picasso.with(DetailsActivity.this).load(model.getImage()).into(viewHolder.imgFood);
                viewHolder.bindToMenu(model);
            }
        };

        rcvMenu.setAdapter(mAdapter);



        //Initialize event on click Favorite button
        imgDetailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteClicked(mStoreReference);
            }
        });

        //Initialize event on click Call
        layoutIconCall.setOnClickListener(onCallClicked);
        layoutCall.setOnClickListener(onCallClicked);

        //Initialize event on click Directions
        layoutIconDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Event click back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Initialize event get data from Firebase
        ValueEventListener storeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                store = dataSnapshot.getValue(Store.class);

                Picasso.with(DetailsActivity.this).load(store.getImage()).into(imgDetailImage);
                mCollapsingToolbarLayout.setTitle(store.getName());
                txtDetailStore.setText(store.getName());
                txtDetailAddress.setText(store.getAddress());
                txtDetailOpen.setText(" " + store.getOpen() + " - " + store.getClose());
                txtDetailPhone.setText(store.getPhone());

                if (store.favorite.containsKey(getUid())) {
                    imgDetailFavorite.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    imgDetailFavorite.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                showGoogleMaps();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadStore:onCancelled", databaseError.toException());
                Toast.makeText(DetailsActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        };

        mStoreReference.addValueEventListener(storeListener);

        mStoreListener = storeListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mStoreListener != null) {
            mStoreReference.removeEventListener(mStoreListener);
        }
    }

    //Event click Favorite
    private void onFavoriteClicked(DatabaseReference storeRef) {
        storeRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Store s = mutableData.getValue(Store.class);

                if (s == null) {
                    return Transaction.success(mutableData);
                }

                if (s.favorite.containsKey(getUid())) {
                    s.favorite.remove(getUid());
                } else {
                    s.favorite.put(getUid(), true);
                }

                mutableData.setValue(s);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    //Event click button Call
    View.OnClickListener onCallClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int accessCallPhone = ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE);

            if(accessCallPhone == PackageManager.PERMISSION_GRANTED){
                startActionCallPhone();
            } else {
                ActivityCompat.requestPermissions(DetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL_PHONE);

            }


        }
    };

    private void showGoogleMaps(){
        //Initialize Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLatLngStore = convertAddresstoLatLng(store.getAddress());
        mMap.addMarker(new MarkerOptions().position(mLatLngStore).draggable(true).title(store.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLngStore));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                intent.putExtra("EXTRA_STORE_LOCAL", mLatLngStore);
            }
        });
    }

    //Convert from address to LatLng
    private LatLng convertAddresstoLatLng (String address){
        LatLng mResult = null;

        Geocoder geocoder = new Geocoder(this);

        List<android.location.Address> mLstAddress = null;
        try {
            mLstAddress = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address mAddress = mLstAddress.get(0);
        mResult = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());

        return mResult;
    }

    private void askCheckPermissions(){
        if(Build.VERSION.SDK_INT >= 23){
            int accessCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            if(accessCallPhone != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL_PHONE);
            }
            startActionCallPhone();
            return;
        }
        startActionCallPhone();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSION_REQUEST_CALL_PHONE){
            if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, R.string.static_Permissions_Granted, Toast.LENGTH_SHORT).show();

                startActionCallPhone();
            } else {
                Toast.makeText(this, R.string.static_Permissions_Denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startActionCallPhone(){
        Uri uri = Uri.parse("tel:" + store.getPhone());
        Intent intent = new Intent(Intent.ACTION_CALL).setData(uri);
        startActivity(intent);
    }
}