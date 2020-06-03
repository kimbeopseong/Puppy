package com.example.puppy.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppy.AddCatActivity;
import com.example.puppy.Cat;
import com.example.puppy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageView ivAddCat;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Cat> arrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment(){ }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView)root.findViewById(R.id.rvCat);
        ivAddCat = (ImageView)root.findViewById(R.id.ivAddCat);
        ivAddCat.setColorFilter(Color.parseColor("#D2C4FF"), PorterDuff.Mode.SRC_IN);
        ivAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCatActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}