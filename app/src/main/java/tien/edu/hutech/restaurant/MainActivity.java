package tien.edu.hutech.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import tien.edu.hutech.models.Menu;
import tien.edu.hutech.models.Store;
import tien.edu.hutech.viewholder.StoreViewHolder;


public class MainActivity extends BaseActivity {

    //Define database reference
    private DatabaseReference mDatabase;

    //Define recycler view
    private FirebaseRecyclerAdapter<Store, StoreViewHolder> mAdapter;
    private RecyclerView recycler_stores;
    private LinearLayoutManager mManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

/*        Store store = new Store();
        store.setImage("https://media.foody.vn/res/g5/42888/prof/s480x300/foody-mobile-hanuri-svh-mb-jpg-698-635742136356152649.jpg");
        store.setAddress("405A Sư Vạn Hạnh P.12 , Quận 10, TP. HCM");
        store.setName("Ăn Vặt Quán Ngon");
        store.setOpen("7:00");
        store.setClose("18:00");
        store.setPhone("+84 989 112 644");

        for(int i = 0; i < 50; i++) {
            mDatabase.child("stores").push().setValue(store);
        }*/
        //Add view
        recycler_stores = (RecyclerView) findViewById(R.id.recycler_stores);
        recycler_stores.setHasFixedSize(true);

        mManager = new LinearLayoutManager(MainActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_stores.setLayoutManager(mManager);

        final Query storeQuery = mDatabase.child("stores").limitToFirst(100);

        mAdapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(
                Store.class,
                R.layout.item,
                StoreViewHolder.class,
                storeQuery) {
            @Override
            protected void populateViewHolder(StoreViewHolder viewHolder, Store model, int position) {
                final DatabaseReference storeRef = getRef(position);

                final String storeKey = storeRef.getKey();

                for(int i = 0; i < 5; i++) {
                    Menu menu = new Menu("Cơm bò bulgogi bokkum", 48000, "https://media.foody.vn/res/g5/42888/s600x600/201682018046-com-bo-bulgogi-bokkum.jpg", storeKey);
                    Menu menu1 = new Menu("Cơm phô mai kim chi", 50000, "https://www.deliverynow.vn/content/images/no-image.png", storeKey);
                    Menu menu2 = new Menu("Cơm chu mok", 37000, "https://media.foody.vn/res/g5/42888/s600x600/20168201817-com-chu-mok.jpg", storeKey);
                    Menu menu3 = new Menu("Cơm ke ran", 37000, "https://media.foody.vn/res/g5/42888/s600x600/201682018122-com-ke-ran.jpg", storeKey);
                    mDatabase.child("menus").push().setValue(menu);
                    mDatabase.child("menus").push().setValue(menu1);
                    mDatabase.child("menus").push().setValue(menu2);
                     mDatabase.child("menus").equals(menu);
                    mDatabase.child("menus").push().setValue(menu3);

                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, storeKey);
                        startActivity(intent);
                    }
                });

                Picasso.with(MainActivity.this).load(model.getImage()).into(viewHolder.imgStoreImage);

                if(model.favorite.containsKey(getUid())){
                    viewHolder.imgStoreFavorite.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.imgStoreFavorite.setImageResource(R.drawable.ic_toggle_star_outline_24);
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
