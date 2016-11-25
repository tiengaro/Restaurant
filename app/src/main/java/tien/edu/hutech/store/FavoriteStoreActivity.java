package tien.edu.hutech.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tien.edu.hutech.models.Store;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.viewholder.StoreViewHolder;

public class FavoriteStoreActivity extends BaseActivity {

    RecyclerView recycler_stores;
    AdapterStore adapterStore;
    ArrayList<Store> stores;
    ArrayList<String> mKeyStores;
    private LinearLayoutManager mManager;
    private DatabaseReference mData;

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
        setContentView(R.layout.activity_favorite_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Favorite");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        stores = new ArrayList<>();
        mKeyStores = new ArrayList<>();

        recycler_stores = (RecyclerView) findViewById(R.id.recycler_stores);
        mManager = new LinearLayoutManager(FavoriteStoreActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_stores.setLayoutManager(mManager);

        adapterStore = new AdapterStore(stores, mKeyStores);
        recycler_stores.setAdapter(adapterStore);

        getData();
    }

    private void getData(){
        stores.clear();
        mKeyStores.clear();

        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("stores").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Store store = dataSnapshot.getValue(Store.class);
                if(store.favorite.containsKey(getUid())){
                    String keyStore = dataSnapshot.getKey();
                    mKeyStores.add(keyStore);
                    stores.add(store);
                    adapterStore.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class AdapterStore extends RecyclerView.Adapter<StoreViewHolder> {

        ArrayList<Store> stores;
        ArrayList<String> mKeyStores;

        public AdapterStore(ArrayList<Store> stores, ArrayList<String> mKeyStores) {
            this.stores = stores;
            this.mKeyStores = mKeyStores;
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

            final String storeKey = mKeyStores.get(position);

            final DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference("stores").child(storeKey);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FavoriteStoreActivity.this, DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, model);
                    startActivity(intent);
                }
            });

            Picasso.with(FavoriteStoreActivity.this).load(model.getImage()).into(viewHolder.imgStoreImage);

            if(model.favorite.containsKey(getUid())){
                viewHolder.imgStoreFavorite.setImageResource(R.drawable.favorite);
            } else {
                viewHolder.imgStoreFavorite.setImageResource(R.drawable.unfavorite);
            }

            viewHolder.bindToStore(model, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Store s = mutableData.getValue(Store.class);

                            if(s == null) {
                                return Transaction.success(mutableData);
                            }

                            if(s.favorite.containsKey(getUid())) {
                                s.favorite.remove(getUid());
                                stores.remove(model);
                            } else {
                                s.favorite.put(getUid(), true);
                                model.favorite.put(getUid(), true);
                            }
                            mutableData.setValue(s);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
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

