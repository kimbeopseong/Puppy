package com.example.puppy.ui.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.puppy.MainActivity;
import com.example.puppy.R;

public class CameraFragment extends Activity implements View.OnClickListener {

    public static CameraFragment getInstance(){
        CameraFragment f = new CameraFragment();
        return f;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_cam);

        Button btnCam = (Button) findViewById(R.id.btn_camera);
        Button btnGal = (Button) findViewById(R.id.btn_gallery);
        btnCam.setOnClickListener(this);
        btnGal.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}