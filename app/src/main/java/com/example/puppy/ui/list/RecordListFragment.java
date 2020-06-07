package com.example.puppy.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class RecordListFragment extends AppCompatActivity implements RecycleAdapter.OnListItemClick {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;
    private String currentUID;
    private String currentPID;

    FirebaseFirestore db;

    CollectionReference poopData;
    Query query;

    public RecordListFragment(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        currentPID = intent.getStringExtra("pid");

        db = FirebaseFirestore.getInstance();
        poopData = db.collection("Pet").document(currentPID).collection("PoopData");

        //Query for read the dataset
//        query = pet.whereEqualTo("UID",currentUID);
//        query = poopData;
        PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(3).build();

        //RecyclerOptions
        FirestorePagingOptions<RecordItem> options = new FirestorePagingOptions.Builder<RecordItem>()
                .setLifecycleOwner(this)
                .setQuery(poopData, config, new SnapshotParser<RecordItem>() {
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

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);

    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked an item: " + position + ", id:" + snapshot.getId());
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("itemId", snapshot.getId());
        intent.putExtra("pid", currentPID);
        startActivity(intent);
    }

}
