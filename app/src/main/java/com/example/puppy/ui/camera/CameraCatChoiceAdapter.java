package com.example.puppy.ui.camera;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.Cat;
import com.example.puppy.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CameraCatChoiceAdapter extends FirestorePagingAdapter<Cat, CameraCatChoiceAdapter.ViewHolder> {

private String choiced_pet_uri;
private OnListItemClick onListItemClick;

public CameraCatChoiceAdapter(@NonNull FirestorePagingOptions<Cat> options, OnListItemClick onListItemClick){
        super(options);
        this.onListItemClick = onListItemClick;
        }

public interface OnListItemClick {
    void onItemClick(DocumentSnapshot snapshot, int position);
}

    @Override
    protected void onBindViewHolder(@NonNull final CameraCatChoiceAdapter.ViewHolder holder, int position, @NonNull Cat model) {
        holder.choiceName.setText(model.getCatName());
        choiced_pet_uri = model.getProfile();
        Picasso.get().load(choiced_pet_uri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.default_profile_image)
                .resize(0,70)
                .into(holder.choiceProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(choiced_pet_uri)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .resize(0,70)
                                .into(holder.choiceProfile);
                    }
                });
    }

    @NonNull
    @Override
    public CameraCatChoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_choice_item, parent, false);
        return new CameraCatChoiceAdapter.ViewHolder(view);
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

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    CircleImageView choiceProfile;
    TextView choiceName;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        choiceProfile = (CircleImageView) itemView.findViewById(R.id.choice_profile);
        choiceName = (TextView) itemView.findViewById(R.id.choice_name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
    }
}
}
