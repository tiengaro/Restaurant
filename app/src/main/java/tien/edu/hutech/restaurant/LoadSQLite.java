package tien.edu.hutech.restaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.models.Store;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lvant on 25/11/2016.
 */

public class LoadSQLite {

    String DATABASE_NAME = "store.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    Context context;
    public static SQLiteDatabase database = null;

    public LoadSQLite(Context context) {
        this.context = context;
        xuLySaoChepCSDLTuAssetsVaoHeThongMobile();
        database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //database.delete("stores", null, null);
        //database.delete("menus", null, null);
    }

    public ArrayList<Store> xuLySaoChepStore() {
        Cursor cursor = database.query("stores", null, null, null, null, null, null);
        return layDataStore(cursor);
    }

    public ArrayList<Store> xuLySaoChepStoreTheoQuan(String mDistrict) {
        Cursor cursor = database.query("stores", null, "district = ?", new String[] {mDistrict}, null, null, null);
        return layDataStore(cursor);
    }

    public ArrayList<Store> xuLySaoChepStoreTheoBrand(String mBrand) {
        Cursor cursor = database.query("stores", null, "brand = ?", new String[] {mBrand}, null, null, null);
        return layDataStore(cursor);
    }

    public ArrayList<Store> xuLySaoChepTimKiemStore(String keyWord) {
        String query = "%" + keyWord + "%";
        Cursor cursor = database.rawQuery("SELECT * FROM stores WHERE name like ?", new String[] {query});
        return layDataStore(cursor);
    }

    public ArrayList<MenuStore> xuLySaoChepMenu() {
        Cursor cursor = database.query("menus", null, null, null, null, null, null);
        return layDataMenu(cursor);
    }

    public ArrayList<MenuStore> xuLySaoChepMenuTheoStore(String mBrand) {
        Cursor cursor = database.rawQuery("SELECT * FROM menus WHERE brand=?", new String[] {mBrand});
        return layDataMenu(cursor);
    }

    public void xuLyInsertStores(ArrayList<Store> stores){
        for (Store store : stores){
            ContentValues contentValues = new ContentValues();
            contentValues.put("keyStore", store.getKeyStore());
            contentValues.put("name", store.getName());
            contentValues.put("address", store.getAddress());
            contentValues.put("district", store.getDistrict());
            contentValues.put("phone", store.getPhone());
            contentValues.put("image", store.getImage());
            contentValues.put("open", store.getOpen());
            contentValues.put("close", store.getClose());
            contentValues.put("brand", store.getBrand());
            contentValues.put("lat", store.getLat());
            contentValues.put("lng", store.getLng());
            try {
                database.insert("stores", null, contentValues);
            }
            catch (Exception e){
                contentValues.remove("keyStore");
                database.update("stores", contentValues, "keyStore = ?", new String[] {store.getKeyStore()});
            }
        }
    }

    public void xuLyInsertMenus(ArrayList<MenuStore> menus){
        for (MenuStore menu : menus){
            ContentValues contentValues = new ContentValues();
            contentValues.put("keyMenu", menu.getKeyMenu());
            contentValues.put("brand", menu.getBrand());
            contentValues.put("name", menu.getName());
            contentValues.put("price", menu.getPrice());
            contentValues.put("image", menu.getImage());
            try {
                database.insert("menus", null, contentValues);
            }
            catch (Exception e) {
                contentValues.remove("keyMenu");
                database.update("menus", contentValues, "keyMenu = ?", new String[] {menu.getKeyMenu()});
            }
        }
    }

    public void xuLyInsertStore(Store store){
        ContentValues contentValues = new ContentValues();
        contentValues.put("keyStore", store.getKeyStore());
        contentValues.put("name", store.getName());
        contentValues.put("address", store.getAddress());
        contentValues.put("district", store.getDistrict());
        contentValues.put("phone", store.getPhone());
        contentValues.put("image", store.getImage());
        contentValues.put("open", store.getOpen());
        contentValues.put("close", store.getClose());
        contentValues.put("brand", store.getBrand());
        contentValues.put("lat", store.getLat());
        contentValues.put("lng", store.getLng());
        //contentValues.put("imageBitmap", store.getBytesImage());
        try {
            if(database.insert("stores", null, contentValues) == -1){
                Log.e("insertStore", "false");
                contentValues.remove("keyStore");
                if(database.update("stores", contentValues, "keyStore = ?", new String[] {store.getKeyStore()}) == -1)
                    Log.e("updateStore", "false");
                else
                    Log.e("updateStore", "success");
            } else {
                Log.e("insertStore", "success");
            }
        }
        catch (Exception e){
            Log.e("Database", e.toString());
        }
    }

    public void xuLyInsertMenu(MenuStore menu){
        ContentValues contentValues = new ContentValues();
        contentValues.put("keyMenu", menu.getKeyMenu());
        contentValues.put("brand", menu.getBrand());
        contentValues.put("name", menu.getName());
        contentValues.put("price", menu.getPrice());
        contentValues.put("image", menu.getImage());
        //contentValues.put("imageBitmap", menu.getBytesImage());
        try {
            if(database.insert("menus", null, contentValues) == -1){
                Log.e("insertMenu", "failse");
                contentValues.remove("keyMenu");
                if(database.update("menus", contentValues, "keyMenu = ?", new String[] {menu.getKeyMenu()}) == -1)
                    Log.e("updateMenu", "false");
                else
                    Log.e("updateMenu", "success");

            } else {
                Log.e("insertMenu", "success");
            }
        }
        catch (Exception e) {
            Log.e("Database", e.toString());
        }
    }

    private ArrayList<Store> layDataStore(Cursor cursor){
        ArrayList<Store> stores = new ArrayList<>();
        while (cursor.moveToNext()) {
            String key = cursor.getString(0);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            String district = cursor.getString(3);
            String phone = cursor.getString(4);
            String image = cursor.getString(5);
            String open = cursor.getString(6);
            String close = cursor.getString(7);
            String brand = cursor.getString(8);
            Double lat = cursor.getDouble(9);
            Double lng = cursor.getDouble(10);
            //byte[] imageBitMap = cursor.getBlob(11);

            Store store = new Store();
            store.setKeyStore(key);
            store.setName(name);
            store.setDistrict(district);
            store.setAddress(address);
            store.setBrand(brand);
            store.setOpen(open);
            store.setClose(close);
            store.setImage(image);
            store.setLat(lat);
            store.setLng(lng);
            store.setPhone(phone);
            //store.setBytesImage(imageBitMap);

            stores.add(store);
        }
        cursor.close();
        return stores;
    }

    private ArrayList<MenuStore> layDataMenu(Cursor cursor){
        ArrayList<MenuStore> menus = new ArrayList<>();
        while (cursor.moveToNext()) {
            String key = cursor.getString(0);
            String name = cursor.getString(2);
            String image = cursor.getString(4);
            String brand = cursor.getString(1);
            int price = (int) cursor.getDouble(3);

            MenuStore menu = new MenuStore();
            menu.setKeyMenu(key);
            menu.setName(name);
            menu.setImage(image);
            menu.setBrand(brand);
            menu.setPrice(price);

            menus.add(menu);
        }
        cursor.close();
        return menus;
    }

    private void xuLySaoChepCSDLTuAssetsVaoHeThongMobile() {
        //Lấy đường dẫn tới tên database trong hệ thống
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        //Xét xem nếu tồn tại tên database đó thì xử lý
        if(!dbFile.exists()){
            try{
                //nếu chưa tồn tại database thì bắt đầu sao chép database từ Assets vào hệ thống
                CopyDataBaseFromAsset();
                //Toast.makeText(this, "Sao chép CSDL vào hệ thống thành công!.", Toast.LENGTH_LONG).show();
            }
            catch (Exception ex){
                //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
        try{
            //Đưa CSDL trong assets sang InputStream để bắt đầu sao chép
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            //Lấy đường dẫn databases
            String outFileName = layDuongDanLuuTru();
            //Tạo 1 file truy xuất đến đường dẫn /databases/
            File f = new File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            //Kiểm tra xem đường dẫn đó có tồn tại không
            if(!f.exists()){
                //nếu không tồn tại thì tạo đường dẫn đó ra
                f.mkdir();
            }
            //Tạo OutputStream với đầu ra là đường dẫn databases
            OutputStream myOutPut = new FileOutputStream(outFileName);
            //tạo 1 mảng byte để đưa từng dữ liệu vào
            byte[] buffer = new byte[1024];
            int lenght;
            //Chạy vòng lặp cho tới khi đọc hết InputStream
            while ((lenght = myInput.read(buffer)) > 0){
                //Ghi vào OutputStream
                myOutPut.write(buffer, 0, lenght);
            }
            myOutPut.flush();
            myInput.close();
            myOutPut.close();
        }
        catch (Exception ex){
            Log.e("Loi_SaoChep: ", ex.toString());
        }
    }

    private String layDuongDanLuuTru(){
        //Trả về đường dẫn của database trong hệ thống
        return context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

}
