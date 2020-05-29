package com.example.puppy.ui.home;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.puppy.Cat;
import com.example.puppy.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    private ArrayList<Cat> arrayList;
    private Context context;

    public CatAdapter(ArrayList<Cat> arrayList, Context context) {
        arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item, parent, false);
        CatViewHolder holder = new CatViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.ivCat);
        holder.tvCName.setText(arrayList.get(position).getCatName());
        holder.tvCAge.setText(arrayList.get(position).getCatAge());
        holder.tvCSpe.setText(arrayList.get(position).getCatSpecies());
        holder.tvCSex.setText(arrayList.get(position).getCatSex());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CatViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCat;
        TextView tvCName;
        TextView tvCAge;
        TextView tvCSpe;
        TextView tvCSex;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivCat = itemView.findViewById(R.id.ivCat);
            this.tvCName = itemView.findViewById(R.id.tvCName);
            this.tvCAge = itemView.findViewById(R.id.tvCAge);
            this.tvCSpe = itemView.findViewById(R.id.tvCSpe);
            this.tvCSex = itemView.findViewById(R.id.tvCSex);
        }
    }
}
