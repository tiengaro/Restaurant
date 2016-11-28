package tien.edu.hutech.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tien.edu.hutech.Auth.SignInActivity;
import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.models.User;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.LoadSQLite;
import tien.edu.hutech.restaurant.R;
import tien.edu.hutech.viewholder.ListMenusViewHolder;

public class FoodActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Define database reference
    private DatabaseReference mDatabase;

    //Define recycler view
    private FirebaseRecyclerAdapter<MenuStore, ListMenusViewHolder> mAdapter;
    private RecyclerView recycler_List_Foods;
    private LinearLayoutManager mManager;
    private String mStoreName;

    private DrawerLayout drawer;

    private TextView nadrawer_loginheader_name;
    private Button navdrawer_loginheader_arrow;
    private TextView        nadrawer_loginheader_email;
    private CircleImageView nadrawer_loginheader_picture;
    private ArrayList<MenuStore> menus;
    private AdapterFood adapterFood;

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
        setContentView(R.layout.activity_food);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Foods");
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

        recycler_List_Foods = (RecyclerView) findViewById(R.id.recycler_List_Foods);
        recycler_List_Foods.setHasFixedSize(true);

        mManager = new LinearLayoutManager(FoodActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recycler_List_Foods.setLayoutManager(mManager);

        Boolean isConnected = checkNetwork();
        if(isConnected){
            getUser();

            //Create database reference
            mDatabase = FirebaseDatabase.getInstance().getReference();

            navdrawer_loginheader_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    Intent intent = new Intent(FoodActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            });

            final Query menusQuery = mDatabase.child("menus").limitToFirst(100);

            mAdapter = new FirebaseRecyclerAdapter<MenuStore, ListMenusViewHolder>(
                    MenuStore.class,
                    R.layout.item_list_food,
                    ListMenusViewHolder.class,
                    menusQuery) {
                @Override
                protected void populateViewHolder(final ListMenusViewHolder viewHolder, final MenuStore model, int position) {

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FoodActivity.this, StoreByFoodActivity.class);
                            intent.putExtra(StoreByFoodActivity.EXTRA_BRAND, model.getBrand());
                            startActivity(intent);
                        }
                    });

                    Picasso.with(FoodActivity.this).load(model.getImage()).into(viewHolder.img_Item_Food);

                    viewHolder.bindToMenu(model);
                }
            };

            recycler_List_Foods.setAdapter(mAdapter);
        }
        else {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            LoadSQLite loadSQLite = new LoadSQLite(this);
            menus = loadSQLite.xuLySaoChepMenu();
            adapterFood = new AdapterFood(menus);
            recycler_List_Foods.setAdapter(adapterFood);
        }

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
                    Intent intent = new Intent(FoodActivity.this, StoreByFoodActivity.class);
                    intent.putExtra(StoreByFoodActivity.EXTRA_BRAND, model.getBrand());
                    startActivity(intent);
                }
            });

            //Picasso.with(FoodActivity.this).load(model.getImage()).into(viewHolder.img_Item_Food);
            viewHolder.img_Item_Food.setImageDrawable(getResources().getDrawable(R.drawable.nopicture));

            viewHolder.bindToMenu(model);
        }

        @Override
        public int getItemCount() {
            return mMenus.size();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home){
            finish();
        }
        else if(id == R.id.nav_search){
            Intent intent = new Intent(FoodActivity.this, SearchFoodActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
