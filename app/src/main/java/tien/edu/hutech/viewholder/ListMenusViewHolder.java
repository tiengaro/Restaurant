package tien.edu.hutech.viewholder;

import android.icu.text.NumberFormat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import tien.edu.hutech.models.MenuStore;
import tien.edu.hutech.restaurant.R;

/**
 * Created by lvant on 12/11/2016.
 */

public class ListMenusViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_Item_Name_Food;
    public TextView txt_Item_Name_Store;
    public TextView txt_Item_Price_Food;
    public ImageView img_Item_Food;

    public ListMenusViewHolder(View itemView) {

        super(itemView);

        img_Item_Food       = (ImageView) itemView.findViewById(R.id.img_Item_Food);
        txt_Item_Name_Food  = (TextView) itemView.findViewById(R.id.txt_Item_Name_Food);
        txt_Item_Name_Store = (TextView) itemView.findViewById(R.id.txt_Item_Name_Store);
        txt_Item_Price_Food = (TextView) itemView.findViewById(R.id.txt_Item_Price_Food);
    }

    public void bindToMenu(MenuStore menu, String nameStore) {
        txt_Item_Name_Food.setText(menu.getName().toString());
        txt_Item_Name_Store.setText(nameStore);
        String price = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            price = NumberFormat.getNumberInstance(Locale.US).format(menu.getPrice());
        }
        txt_Item_Price_Food.setText(price);

    }
}
