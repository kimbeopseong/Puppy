package com.example.puppy.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import com.example.puppy.R;

public class CameraFragment extends AppCompatActivity {
    public static Intent intent;
    public static AppCompatActivity cameraFragment;

    public static float sDensity;

    public static CameraFragment getInstance(){
        CameraFragment f = new CameraFragment();
        return f;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_cam);
        sDensity = getApplicationContext().getResources().getDisplayMetrics().density;
        cameraFragment = CameraFragment.this;


        intent=getIntent();

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

}