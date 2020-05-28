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

public class HomeFragment extends ListFragment {

    ListViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        adapter= new ListViewAdapter();
        setListAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_face_black_24dp),
                "나폴레옹",14);

        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_face_black_24dp),
                "세종대왕",12);
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_face_black_24dp),
                "징기스칸",11);

        return super.onCreateView(inflater,container,savedInstanceState);
    }
}