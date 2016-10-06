package tien.edu.hutech.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tien.edu.hutech.models.Menu;
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

    public void bindToMenu (Menu menu){
        txtFoodName.setText(menu.getName());
        txtFoodPrice.setText(String.valueOf(menu.getPrice()));
    }
}
