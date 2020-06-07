package com.example.puppy.ui.camera;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.Cat;
import com.example.puppy.PicassoTransformations;
import com.example.puppy.R;
import com.example.puppy.ui.list.RecordCatChoice;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CameraCatChoice extends Fragment {

    private static final String CLF_TAG = "Cat Camera Fragment";

    private View choice_recordView;
    private RecyclerView catChoiceList;

    private FirebaseAuth mAuth;
    private String currentUID;
    private String choiced_pet_name, choiced_pet_uri;
    private FirebaseFirestore db;


    public CameraCatChoice(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        choice_recordView = inflater.inflate(R.layout.cat_choice_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

        catChoiceList = (RecyclerView) choice_recordView.findViewById(R.id.cat_choice_view);
        catChoiceList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        catChoiceList.addItemDecoration(new ItemDecoration(2, 50));


        return choice_recordView;
    }

    @Override
    public void onStart() {
        super.onStart();

        PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(6).setPageSize(3).build();

        FirestorePagingOptions<Cat> options = new FirestorePagingOptions.Builder<Cat>()
                .setLifecycleOwner(this)
                .setQuery(db.collection("Pet").whereEqualTo("p_ID", currentUID), config, new SnapshotParser<Cat>() {
                    @NonNull
                    @Override
                    public Cat parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Cat cat = snapshot.toObject(Cat.class);
                        cat.setCatName(snapshot.get("p_name").toString());
                        cat.setProfile(snapshot.get("p_uri").toString());
                        final String choiced_catId = snapshot.getId();
                        return cat;
                    }
                }).build();

        FirestorePagingAdapter<Cat, CameraCatChoice.ChoiceViewHolder> choiceAdapter =
                new FirestorePagingAdapter<Cat, CameraCatChoice.ChoiceViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final CameraCatChoice.ChoiceViewHolder holder, int position, @NonNull Cat model) {
                        holder.choiceName.setText(model.getCatName());
                        choiced_pet_uri = model.getProfile();
                        PicassoTransformations.targetWidth = 70;
                        Picasso.get().load(choiced_pet_uri)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .transform(PicassoTransformations.resizeTransformation)
                                .into(holder.choiceProfile, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        PicassoTransformations.targetWidth = 70;
                                        Picasso.get().load(choiced_pet_uri)
                                                .placeholder(R.drawable.default_profile_image)
                                                .error(R.drawable.default_profile_image)
                                                .transform(PicassoTransformations.resizeTransformation)
                                                .into(holder.choiceProfile);
                                    }
                                });
                    }

                    @NonNull
                    @Override
                    public CameraCatChoice.ChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_choice_item, parent, false);
                        return new CameraCatChoice.ChoiceViewHolder(view);
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
                };
        catChoiceList.setAdapter(choiceAdapter);
    }

    public static class ChoiceViewHolder extends RecyclerView.ViewHolder{
        CircleImageView choiceProfile;
        TextView choiceName;

        public ChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            choiceProfile = (CircleImageView) itemView.findViewById(R.id.choice_profile);
            choiceName = (TextView) itemView.findViewById(R.id.choice_name);
        }
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public ItemDecoration(int spanCount, int spacing){
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (column < 1){
                outRect.right = spacing - (column + 1) * spacing / spanCount;
            } else {
                outRect.right = 0;
            }

            outRect.bottom = spacing;
        }
    }
}
