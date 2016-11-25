package tien.edu.hutech.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import tien.edu.hutech.Auth.AccountFragment;
import tien.edu.hutech.Auth.SignInActivity;
import tien.edu.hutech.models.User;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference mData;
    private StorageReference mStorage;
    private DrawerLayout        drawer;
    private NavigationView      navigationView;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager     fragmentManager;

    private TextView        nadrawer_loginheader_name;
    private Button          navdrawer_loginheader_arrow;
    private TextView        nadrawer_loginheader_email;
    private CircleImageView nadrawer_loginheader_picture;

    Toolbar toolbar;

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
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        fragmentManager = getSupportFragmentManager();

        loadSelection(R.id.nav_home);

        navigationView.setCheckedItem(R.id.nav_home);

        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        nadrawer_loginheader_name       = (TextView) headerLayout.findViewById(R.id.nadrawer_loginheader_name);
        navdrawer_loginheader_arrow     = (Button) headerLayout.findViewById(R.id.navdrawer_loginheader_arrow);
        nadrawer_loginheader_email      = (TextView) headerLayout.findViewById(R.id.nadrawer_loginheader_email);
        nadrawer_loginheader_picture    = (CircleImageView) headerLayout.findViewById(R.id.nadrawer_loginheader_picture);

        navdrawer_loginheader_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        getUser();
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
    private void loadSelection (int id){
        switch (id){
            case R.id.nav_home:
                toolbar.setTitle("Home");
                fragmentTransaction = fragmentManager.beginTransaction();
                HomeFragment homePageFragment = new HomeFragment();
                fragmentTransaction.replace(R.id.fragment_home_page, homePageFragment).commit();
                break;
            case R.id.nav_account:
                toolbar.setTitle("My Account");
                fragmentTransaction = fragmentManager.beginTransaction();
                AccountFragment myAccountFragment = new AccountFragment();
                fragmentTransaction.replace(R.id.fragment_home_page, myAccountFragment).commit();
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_account:
                loadSelection(id);
                break;
            case R.id.nav_home:
                loadSelection(id);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
