package tien.edu.hutech.store;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tien.edu.hutech.Auth.SignInActivity;
import tien.edu.hutech.models.Store;
import tien.edu.hutech.models.User;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.LoadSQLite;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.viewholder.StoreViewHolder;

public class StoreActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static SQLiteDatabase database = null;

    //Define database reference
    private DatabaseReference mDatabase;

    //Define recycler view
    private FirebaseRecyclerAdapter<Store, StoreViewHolder> mAdapter;
    private AdapterStore adapterStore;
    private RecyclerView recycler_stores;
    private LinearLayoutManager mManager;
    private DrawerLayout drawer;

    private TextView nadrawer_loginheader_name;
    private Button navdrawer_loginheader_arrow;
    private TextView        nadrawer_loginheader_email;
    private CircleImageView nadrawer_loginheader_picture;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.cleanup();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stores");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        nadrawer_loginheader_name       = (TextView) headerLayout.findViewById(R.id.nadrawer_loginheader_name);
        navdrawer_loginheader_arrow     = (Button) headerLayout.findViewById(R.id.navdrawer_loginheader_arrow);
        nadrawer_loginheader_email      = (TextView) headerLayout.findViewById(R.id.nadrawer_loginheader_email);
        nadrawer_loginheader_picture    = (CircleImageView) headerLayout.findViewById(R.id.nadrawer_loginheader_picture);
        getUser();

        navdrawer_loginheader_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(StoreActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        //Add view
        recycler_stores = (RecyclerView) findViewById(R.id.recycler_stores);
        recycler_stores.setHasFixedSize(true);

        mManager = new LinearLayoutManager(StoreActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_stores.setLayoutManager(mManager);

        boolean isConnected = checkNetwork();
        if(isConnected) {

            //Create database reference
            mDatabase = FirebaseDatabase.getInstance().getReference();

/*        xuLySaoChepCSDLTuAssetsVaoHeThongMobile();
        mStores = new ArrayList<>();
        mMenus = new ArrayList<>();
        xuLySaoChepStore();
        xuLySaoChepMenu();
        for (int i = 0; i < mStores.size(); i++){
            mDatabase.child("stores").push().setValue(mStores.get(i));

        }
        for(int j = 0; j < mMenus.size(); j++){
            mDatabase.child("menus").push().setValue(mMenus.get(j));
        }*/
/*        Store store = new Store();
        store.setImage("https://media.foody.vn/res/g5/42888/prof/s480x300/foody-mobile-hanuri-svh-mb-jpg-698-635742136356152649.jpg");
        store.setAddress("405A Sư Vạn Hạnh P.12 , Quận 10, TP. HCM");
        store.setName("Ăn Vặt Quán Ngon");
        store.setOpen("7:00");
        store.setClose("18:00");
        store.setPhone("+84 989 112 644");
        store.setDistrict("Quận 10");

        for(int i = 0; i < 50; i++) {
            mDatabase.child("stores").push().setValue(store);
        }*/
            final Query storeQuery = mDatabase.child("stores").limitToFirst(100);

            mAdapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(
                    Store.class,
                    R.layout.item,
                    StoreViewHolder.class,
                    storeQuery) {
                @Override
                protected void populateViewHolder(StoreViewHolder viewHolder, final Store model, int position) {
                    final DatabaseReference storeRef = getRef(position);

                    final String storeKey = storeRef.getKey();
                    model.setKeyStore(storeKey);
/*                for(int i = 0; i < 1; i++) {
                    MenuStore menu = new MenuStore("Cơm bò bulgogi bokkum", 48000, "https://media.foody.vn/res/g5/42888/s600x600/201682018046-com-bo-bulgogi-bokkum.jpg", storeKey);
                    MenuStore menu1 = new MenuStore("Cơm phô mai kim chi", 50000, "https://www.deliverynow.vn/content/images/no-image.png", storeKey);
                    MenuStore menu2 = new MenuStore("Cơm chu mok", 37000, "https://media.foody.vn/res/g5/42888/s600x600/20168201817-com-chu-mok.jpg", storeKey);
                    MenuStore menu3 = new MenuStore("Cơm ke ran", 37000, "https://media.foody.vn/res/g5/42888/s600x600/201682018122-com-ke-ran.jpg", storeKey);
                    mDatabase.child("menus").push().setValue(menu);
                    mDatabase.child("menus").push().setValue(menu1);
                    mDatabase.child("menus").push().setValue(menu2);
                    mDatabase.child("menus").push().setValue(menu3);

                }*/
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StoreActivity.this, DetailsActivity.class);
                            intent.putExtra(DetailsActivity.EXTRA_STORE_KEY, model);
                            startActivity(intent);
                        }
                    });

                    Picasso.with(StoreActivity.this).load(model.getImage()).into(viewHolder.imgStoreImage);

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
            ArrayList<Store> stores = loadSQLite.xuLySaoChepStore();
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
                    Intent intent = new Intent(StoreActivity.this, DetailsActivity.class);
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

    public void getUser() {
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("users").child(getUid());
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mUid = dataSnapshot.getKey();
                if (mUid.equals(getUid())) {
                    user = dataSnapshot.getValue(User.class);
                    nadrawer_loginheader_name.setText(user.getUsername());
                    nadrawer_loginheader_email.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home){
            finish();
        }
        else if(id == R.id.nav_search){
            Intent intent = new Intent(StoreActivity.this, SearchStoreActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_fav){
            Intent intent = new Intent(StoreActivity.this, FavoriteStoreActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_near){
            if(checkNetwork()) {
                Intent intent = new Intent(StoreActivity.this, NearStoreActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(this, "Không thể sử dụng tính năng này khi Offline", Toast.LENGTH_SHORT).show();
        }
        else {
            String mDistrict = item.getTitle().toString();

            Intent intent = new Intent(StoreActivity.this, FilterStoreActivity.class);
            intent.putExtra(FilterStoreActivity.EXTRA_STORE_DISTRICT, mDistrict);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
