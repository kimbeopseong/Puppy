package com.example.puppy.ui.list;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecycleAdapter extends FirestorePagingAdapter<RecordItem, RecycleAdapter.ViewHolder> {

    private OnListItemClick onListItemClick;

    public RecycleAdapter(@NonNull FirestorePagingOptions<RecordItem> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView date;
        private TextView stat;
        private TextView lv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.daily_date);
            stat = itemView.findViewById(R.id.daily_stat);
            lv = itemView.findViewById(R.id.daily_lv);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position, @NonNull RecordItem model) {
        holder.date.setText(model.getDate());
        holder.stat.setText(model.getStat());
        holder.lv.setText(model.getLv()+"");
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_INITIAL:
                Log.d("PAGING_LOG", "Loading Initial Data" );
                break;
            case LOADING_MORE:
                Log.d("PAGING_LOG", "Loading Next Page" );
                break;
            case FINISHED:
                Log.d("PAGING_LOG", "All Data Loaded" );
                break;
            case ERROR:
                Log.d("PAGING_LOG", "Error Loading Data" );
                break;
            case LOADED:
                Log.d("PAGING_LOG", "Total Items Loaded: " + getItemCount());
                break;
        }
    }
}
