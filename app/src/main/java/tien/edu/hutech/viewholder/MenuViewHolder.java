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
 * Created by lvant on 06/10/2016.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder{

    public ImageView imgFood;
    public TextView txtFoodName;
    public TextView txtFoodPrice;


    public MenuViewHolder(View itemView)
    {
        super(itemView);

        imgFood = (ImageView) itemView.findViewById(R.id.imgFood);
        txtFoodName = (TextView) itemView.findViewById(R.id.txtFoodName);
        txtFoodPrice = (TextView) itemView.findViewById(R.id.txtFoodPrice);
    }

    public void bindToMenu (MenuStore menu){
        txtFoodName.setText(menu.getName());
        String price = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            price = NumberFormat.getNumberInstance(Locale.US).format(menu.getPrice());
        }
        txtFoodPrice.setText(price);
    }
}
