package tien.edu.hutech.restaurant;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.models.Store;

/**
 * Created by lvant on 25/11/2016.
 */

public class ConvertImageToBitmap implements Target {

    Store mStore = null;
    MenuStore mMenu = null;

    public ConvertImageToBitmap(Store store, MenuStore menu) {
        mStore = store;
        mMenu = menu;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        if(mStore != null){

            mStore.setBytesImage(data);
        }
        if(mMenu != null){
            mStore.setBytesImage(data);
        }

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
