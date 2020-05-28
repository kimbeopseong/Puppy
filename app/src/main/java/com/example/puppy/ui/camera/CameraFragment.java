package com.example.puppy.ui.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.puppy.R;

public class CameraFragment extends DialogFragment implements View.OnClickListener {

    public static CameraFragment getInstance(){
        CameraFragment f = new CameraFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cam, container, false);
        Button btnCam = (Button) v.findViewById(R.id.btn_camera);
        Button btnGal = (Button) v.findViewById(R.id.btn_gallery);
        btnCam.setOnClickListener(this);
        btnGal.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}