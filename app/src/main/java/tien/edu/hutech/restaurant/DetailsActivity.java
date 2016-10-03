package tien.edu.hutech.restaurant;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import tien.edu.hutech.models.Store;

public class DetailsActivity extends BaseActivity {

    private static final String TAG = "DetailsActivity";
    public static final String EXTRA_STORE_KEY = "store_key";

    //Defind toolbar;
    private Toolbar toolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private DatabaseReference mStoreReference;
    ValueEventListener mStoreListener;
    private String mstoreKey;

    //Define views
    private ImageView imgDetailImage;
    private ImageView imgDetailFavorite;
    private ImageButton btnDetailDirection;
    private ImageButton btnDetailCall;
    private TextView txtDetailStore;
    private TextView txtDetailAddress;
    private TextView txtDetailOpen;
    private TextView txtDetailPhone;

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
        imgDetailImage = (ImageView) findViewById(R.id.imgDetailImage);
        imgDetailFavorite = (ImageView) findViewById(R.id.imgDetailFavorite);
        btnDetailDirection = (ImageButton) findViewById(R.id.btnDetailDirection);
        btnDetailCall = (ImageButton) findViewById(R.id.btnDetailCall);
        txtDetailStore = (TextView) findViewById(R.id.txtDetailStore);
        txtDetailAddress = (TextView) findViewById(R.id.txtDetailAddress);
        txtDetailOpen = (TextView) findViewById(R.id.txtDetailOpen);
        txtDetailPhone = (TextView) findViewById(R.id.txtDetailPhone);

        //Get store key from intent
        mstoreKey = getIntent().getStringExtra(EXTRA_STORE_KEY);

        //Initialize Database
        mStoreReference = FirebaseDatabase.getInstance().getReference()
                .child("stores").child(mstoreKey);

        imgDetailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteClicked(mStoreReference);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener storeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue(Store.class);

                Picasso.with(DetailsActivity.this).load(store.getImage()).into(imgDetailImage);
                mCollapsingToolbarLayout.setTitle(store.getName());
                txtDetailStore.setText(store.getName());
                txtDetailAddress.setText(store.getAddress());
                txtDetailOpen.setText(" " + store.getOpen() + " - " + store.getClose());
                txtDetailPhone.setText(store.getPhone());

                if(store.favorite.containsKey(getUid())){
                    imgDetailFavorite.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    imgDetailFavorite.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadStore:onCancelled", databaseError.toException());
                Toast.makeText(DetailsActivity.this, "Failed to load post.",Toast.LENGTH_SHORT).show();
            }
        };

        mStoreReference.addValueEventListener(storeListener);

        mStoreListener = storeListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mStoreListener != null){
            mStoreReference.removeEventListener(mStoreListener);
        }
    }

    private void onFavoriteClicked(DatabaseReference storeRef) {
        storeRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Store s = mutableData.getValue(Store.class);

                if(s == null) {
                    return Transaction.success(mutableData);
                }

                if(s.favorite.containsKey(getUid())) {
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

}
