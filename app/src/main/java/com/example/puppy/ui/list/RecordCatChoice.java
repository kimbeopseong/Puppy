package com.example.puppy.ui.list;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.Cat;
import com.example.puppy.R;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecordCatChoice extends Fragment implements RecordCatChoiceAdapter.OnListItemClick{

    private static final String CLF_TAG = "Cat List Fragment";

    private View choice_recordView;
    private RecyclerView catChoiceList;

    private FirebaseAuth mAuth;
    private String currentUID;
    private FirebaseFirestore db;

    public RecordCatChoice(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        choice_recordView = inflater.inflate(R.layout.cat_choice_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

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
                        return cat;
                    }
                }).build();

        RecordCatChoiceAdapter choiceAdapter = new RecordCatChoiceAdapter(options, this);

        catChoiceList = (RecyclerView) choice_recordView.findViewById(R.id.cat_choice_view);
        catChoiceList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        catChoiceList.addItemDecoration(new ItemDecoration(2, 50));
        catChoiceList.setAdapter(choiceAdapter);

        return choice_recordView;
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;

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

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked an item: " + position + ", id:" + snapshot.getId());
        Intent intent = new Intent(this.getContext(), RecordListFragment.class);
        intent.putExtra("pid", snapshot.getId());
        startActivity(intent);
    }
}
