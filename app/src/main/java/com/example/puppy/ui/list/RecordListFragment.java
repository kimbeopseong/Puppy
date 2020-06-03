package com.example.puppy.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.MainActivity;
import com.example.puppy.R;
import com.example.puppy.ResultActivity;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class RecordListFragment extends Fragment implements RecycleAdapter.OnListItemClick {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;
    private String currentUID;

    FirebaseFirestore db;
    CollectionReference poopData;
    Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
        poopData = db.collection("PoopData");

        //Query for read the dataset
        query = poopData.whereEqualTo("UID",currentUID);

        PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(3).build();

        //RecyclerOptions
        FirestorePagingOptions<RecordItem> options = new FirestorePagingOptions.Builder<RecordItem>()
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<RecordItem>() {
                    @NonNull
                    @Override
                    public RecordItem parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        RecordItem item = snapshot.toObject(RecordItem.class);
                        String item_id = snapshot.getId();
                        item.setItem_Id(item_id);
                        return item;
                    }
                })
                .build();

        adapter = new RecycleAdapter(options, this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);

        return view;
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked an item: " + position + ", id:" + snapshot.getId());
        Intent intent = new Intent(this.getContext(), ResultActivity.class);
        intent.putExtra("itemId", snapshot.getId());
        startActivity(intent);
    }

}
