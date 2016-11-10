package tien.edu.hutech.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tien.edu.hutech.models.Store;
import tien.edu.hutech.restaurant.R;

/**
 * Created by lvant on 02/10/2016.
 */

public class StoreViewHolder extends RecyclerView.ViewHolder{

    public ImageView    imgStoreImage;
    public ImageView    imgStoreFavorite;
    public TextView     txtStoreName;
    public TextView     txtStoreOpen;
    public TextView     txtStoreAddress;

    public StoreViewHolder(View itemView) {
        super(itemView);

        imgStoreImage       = (ImageView) itemView.findViewById(R.id.imgStoreImage);
        imgStoreFavorite    = (ImageView) itemView.findViewById(R.id.imgStoreFavorite);
        txtStoreName        = (TextView) itemView.findViewById(R.id.txtStoreName);
        txtStoreOpen        = (TextView) itemView.findViewById(R.id.txtStoreOpen);
        txtStoreAddress     = (TextView) itemView.findViewById(R.id.txtStoreAddress);
    }

    public void bindToStore(Store store, View.OnClickListener favoriteClickListener) {
        txtStoreName.setText(store.getName());
        txtStoreAddress.setText(store.getAddress());
        txtStoreOpen.setText(" " + store.getOpen() + " - " + store.getClose());

        imgStoreFavorite.setOnClickListener(favoriteClickListener);
    }
}
