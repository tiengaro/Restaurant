package tien.edu.hutech.restaurant;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.models.Store;

public class LoadDataService extends Service {

    private ArrayList<Store> stores;
    private ArrayList<MenuStore> menus;
    private DatabaseReference mData;
    private LoadSQLite loadSQLite;

    public LoadDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stores = new ArrayList<>();
        menus = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference();
        loadSQLite = new LoadSQLite(this);
    }

    class LoadImgStore extends AsyncTask<Store, Void, Store>{

        @Override
        protected void onPostExecute(Store store) {
            super.onPostExecute(store);
            //loadSQLite.xuLyInsertStore(store);
        }


        @Override
        protected Store doInBackground(Store... params) {
            Store store = params[0];
            try {
                URL url = new URL(store.getImage());
                Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                store.setBytesImage(data);
                loadSQLite.xuLyInsertStore(store);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return store;
        }
    }

    class LoadImgMenu extends AsyncTask<MenuStore, Void, MenuStore>{

        @Override
        protected void onPostExecute(MenuStore menu) {
            super.onPostExecute(menu);
            //loadSQLite.xuLyInsertStore(store);
        }


        @Override
        protected MenuStore doInBackground(MenuStore... params) {
            MenuStore menuStore = params[0];
            try {
                URL url = new URL(menuStore.getImage());
                Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                menuStore.setBytesImage(data);
                loadSQLite.xuLyInsertMenu(menuStore);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return menuStore;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mData.child("stores").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Store store = dataSnapshot.getValue(Store.class);
                store.setKeyStore(dataSnapshot.getKey());
                stores.add(store);
                //ConvertImageToBitmap convertImageToBitmap = new ConvertImageToBitmap(store, null);
                //Picasso.with(getBaseContext()).load(store.getImage()).into(convertImageToBitmap);
                loadSQLite.xuLyInsertStore(store);
//                LoadImgStore loadImgStoreTask = new LoadImgStore();
//                loadImgStoreTask.execute(store);
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
        mData.child("menus").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MenuStore menu = dataSnapshot.getValue(MenuStore.class);
                menu.setKeyMenu(dataSnapshot.getKey());
                menus.add(menu);
                //ConvertImageToBitmap convertImageToBitmap = new ConvertImageToBitmap(null, menu);
                //Picasso.with(getBaseContext()).load(menu.getImage()).into(convertImageToBitmap);
                loadSQLite.xuLyInsertMenu(menu);
//                LoadImgMenu loadImgMenuTask = new LoadImgMenu();
//                loadImgMenuTask.execute(menu);
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
        return START_STICKY;
    }
}
