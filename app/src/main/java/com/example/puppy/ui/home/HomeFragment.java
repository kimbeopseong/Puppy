package com.example.puppy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.puppy.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends ListFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public HomeFragment(){ }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater,container,savedInstanceState);
    }

    public void addCat(){

    }
}