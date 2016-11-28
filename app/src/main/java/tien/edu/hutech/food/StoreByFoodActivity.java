package tien.edu.hutech.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tien.edu.hutech.models.Store;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.LoadSQLite;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.store.DetailsActivity;
import tien.edu.hutech.viewholder.StoreViewHolder;

public class StoreByFoodActivity extends BaseActivity {

    public static final String EXTRA_BRAND = "store_district";

    //Define database reference
    private DatabaseReference mDatabase;

    //Define recycler view
    private FirebaseRecyclerAdapter<Store, StoreViewHolder> mAdapter;
    private RecyclerView recycler_stores;
    private LinearLayoutManager mManager;
    private String mBrand;
    private AdapterStore adapterStore;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_by_food);

        Intent intent = getIntent();
        mBrand = intent.getStringExtra(EXTRA_BRAND);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(mBrand);

        //Add view
        recycler_stores = (RecyclerView) findViewById(R.id.recycler_stores);
        recycler_stores.setHasFixedSize(true);

        mManager = new LinearLayoutManager(StoreByFoodActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_stores.setLayoutManager(mManager);

        Boolean isConnected = checkNetwork();
        if(isConnected){
            //Create database reference
            mDatabase = FirebaseDatabase.getInstance().getReference().child("stores");

            final Query storeQuery = mDatabase.orderByChild("brand").equalTo(mBrand);

            mAdapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(
                    Store.class,
                    R.layout.item,
                    StoreViewHolder.class,
                    storeQuery) {
                @Override
                protected void populateViewHolder(StoreViewHolder viewHolder, final Store model, int position) {
                    final DatabaseReference storeRef = getRef(position);


                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StoreByFoodActivity.this, DetailsActivity.class);
                            intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, model);
                            startActivity(intent);
                        }
                    });

                    Picasso.with(StoreByFoodActivity.this).load(model.getImage()).into(viewHolder.imgStoreImage);

                    if(model.favorite.containsKey(getUid())){
                        viewHolder.imgStoreFavorite.setImageResource(R.drawable.favorite);
                    } else {
                        viewHolder.imgStoreFavorite.setImageResource(R.drawable.unfavorite);
                    }

                    viewHolder.bindToStore(model, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onFavoriteClicked(storeRef);
                        }
                    });


                }
            };

            recycler_stores.setAdapter(mAdapter);
        }
        else {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            LoadSQLite loadSQLite = new LoadSQLite(this);
            ArrayList<Store> stores = loadSQLite.xuLySaoChepStoreTheoBrand(mBrand);
            adapterStore = new AdapterStore(stores);
            recycler_stores.setAdapter(adapterStore);
        }

    }

    public class AdapterStore extends RecyclerView.Adapter<StoreViewHolder> {

        ArrayList<Store> stores;
        public AdapterStore(ArrayList<Store> stores) {
            this.stores = stores;
        }

        @Override
        public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item, parent, false);
            return new StoreViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final StoreViewHolder viewHolder, int position) {
            final Store model = stores.get(position);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StoreByFoodActivity.this, DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, model);
                    Log.e("SearchStore", model.getKeyStore());
                    startActivity(intent);
                }
            });

//            byte[] bytes = model.getBytesImage();
//            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            viewHolder.imgStoreImage.setImageBitmap(bm);
            viewHolder.imgStoreImage.setImageDrawable(getResources().getDrawable(R.drawable.nopicture));
            viewHolder.bindToStore(model, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterStore.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return stores.size();
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
