package tien.edu.hutech.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.store.DetailsActivity;
import tien.edu.hutech.viewholder.ListMenusViewHolder;

public class SearchFoodActivity extends BaseActivity {

    RecyclerView recycler_List_Foods;
    AdapterFood adapterFood;
    ArrayList<MenuStore> mMenus;
    private LinearLayoutManager mManager;
    private DatabaseReference mData;
    private String mStoreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mMenus = new ArrayList<>();

        recycler_List_Foods = (RecyclerView) findViewById(R.id.recycler_List_Foods);
        mManager = new LinearLayoutManager(SearchFoodActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_List_Foods.setLayoutManager(mManager);

        adapterFood = new AdapterFood(mMenus);
        recycler_List_Foods.setAdapter(adapterFood);

    }

    //Create menu search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu, menu);
        MenuItem menuSearch = menu.findItem(R.id.mnu_search);
        SearchView searchView = (SearchView) menuSearch.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.isEmpty() == false){
                    getData(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)   {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void getData(final String mKeyWord){
        mMenus.clear();

        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("menus").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MenuStore mMenu = dataSnapshot.getValue(MenuStore.class);
                if(mMenu.getName().toUpperCase().contains(mKeyWord.toUpperCase())){
                    mMenus.add(mMenu);
                    adapterFood.notifyDataSetChanged();
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

    public class AdapterFood extends RecyclerView.Adapter<ListMenusViewHolder> {

        ArrayList<MenuStore> mMenus;
        public AdapterFood(ArrayList<MenuStore> mMenus) {
            this.mMenus = mMenus;
        }

        @Override
        public ListMenusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_list_food, parent, false);
            return new ListMenusViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListMenusViewHolder viewHolder, int position) {
            final MenuStore model = mMenus.get(position);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchFoodActivity.this, DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, model.getBrand());
                    startActivity(intent);
                }
            });

            Picasso.with(SearchFoodActivity.this).load(model.getImage()).into(viewHolder.img_Item_Food);

            viewHolder.bindToMenu(model);
        }

        @Override
        public int getItemCount() {
            return mMenus.size();
        }

    }

}

