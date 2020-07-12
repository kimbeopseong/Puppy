package com.example.puppy.ui.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.puppy.R;
import com.example.puppy.ResultActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import xyz.hasnat.sweettoast.SweetToast;

public class CameraFragment extends AppCompatActivity implements View.OnClickListener {
    public static Intent intent;
    public static AppCompatActivity cameraFragment;

    public static float sDensity;

    private String TAG = "CAMPRACTICE";
    private int PERMISSIONS_REQUEST_CODE = 100;
    private String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;

    private CameraPreview myCameraPreview = null;
    FrameLayout cameraPreview = null;
    Button btnCapture = null;

    public static CameraFragment getInstance(){
        CameraFragment f = new CameraFragment();
        return f;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.fragment_cam);
        sDensity = getApplicationContext().getResources().getDisplayMetrics().density;
        cameraFragment = CameraFragment.this;

        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);

        intent=getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permissionCheckCamera
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int permissionCheckStorage
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED
                    && permissionCheckStorage == PackageManager.PERMISSION_GRANTED){


//              권한 있는 경우
                Log.d(TAG, "permission Granted !!!");
                startCamera();


            } else {


//              권한 없는 경우
                Log.d(TAG, "No permission !!!");
                ActivityCompat.requestPermissions(this,
                        REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            }
        } else {


//          Marshmallow 이전 버전인 경우, 권한체크 X
            Log.d(TAG, "마시멜로보다 버전이 낮아 권한이 있습니다 !!!");
            startCamera();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "requestCode: " + requestCode + ", grantResults size : " + grantResults.length);

        if (requestCode == PERMISSIONS_REQUEST_CODE){

            boolean check_result = true;

            for (int result: grantResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                startCamera();

            } else{
                Log.d(TAG, "권한이 거부되었습니다 !!!");
            }


        }

    }

    private void startCamera(){
        Log.d(TAG, "startCamera");

        myCameraPreview = new CameraPreview(this, CAMERA_FACING);
        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);
        cameraPreview.addView(myCameraPreview);
    }

    @Override
    public void onClick(View view) {
        myCameraPreview.takePicture();
    }

}