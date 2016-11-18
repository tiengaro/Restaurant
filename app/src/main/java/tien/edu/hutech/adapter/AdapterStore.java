package tien.edu.hutech.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tien.edu.hutech.models.Store;
import tien.edu.hutech.restaurant.R;

/**
 * Created by lvant on 16/11/2016.
 */

public class AdapterStore extends RecyclerView.Adapter<AdapterStore.RecyclerViewHolder> {

    ArrayList<Store> stores;
    ArrayList<String> mKeyStores;

    public AdapterStore(ArrayList<Store> stores, ArrayList<String> mKeyStores) {
        this.stores = stores;
        this.mKeyStores = mKeyStores;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
