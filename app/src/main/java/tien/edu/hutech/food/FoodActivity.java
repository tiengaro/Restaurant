package tien.edu.hutech.food;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.store.DetailsActivity;
import tien.edu.hutech.viewholder.ListMenusViewHolder;

public class FoodActivity extends AppCompatActivity {

    //Define database reference
    private DatabaseReference mDatabase;

    //Define recycler view
    private FirebaseRecyclerAdapter<MenuStore, ListMenusViewHolder> mAdapter;
    private RecyclerView recycler_List_Foods;
    private LinearLayoutManager mManager;
    private String mStoreName;

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
        setContentView(R.layout.activity_food);

        //Create database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recycler_List_Foods = (RecyclerView) findViewById(R.id.recycler_List_Foods);
        recycler_List_Foods.setHasFixedSize(true);

        mManager = new LinearLayoutManager(FoodActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_List_Foods.setLayoutManager(mManager);

        final Query menusQuery = mDatabase.child("menus").limitToFirst(100);

        mAdapter = new FirebaseRecyclerAdapter<MenuStore, ListMenusViewHolder>(
                MenuStore.class,
                R.layout.item_list_food,
                ListMenusViewHolder.class,
                menusQuery) {
            @Override
            protected void populateViewHolder(ListMenusViewHolder viewHolder, MenuStore model, int position) {

                final String storeKey = model.getStoreKey();

                final DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference().child("stores").child(storeKey);

                storeRef.child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mStoreName = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FoodActivity.this, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, storeKey);
                        startActivity(intent);
                    }
                });

                Picasso.with(FoodActivity.this).load(model.getImage()).into(viewHolder.img_Item_Food);

                viewHolder.bindToMenu(model, mStoreName);
            }
        };

        recycler_List_Foods.setAdapter(mAdapter);
    }
}
