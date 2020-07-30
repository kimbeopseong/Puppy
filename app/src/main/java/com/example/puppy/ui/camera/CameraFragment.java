package com.example.puppy.ui.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.puppy.R;

import java.io.File;

public class CameraFragment extends AppCompatActivity implements CallbackInterface {
    public static Intent intent;
    public static AppCompatActivity cameraFragment;

    private TextureView textureView;
    private CameraPreview cameraPreview;
    private Button btnCapture;

    private String TAG = "CAM_FRAGMENT";

    static final int REQUEST_CAM = 1;
    static final int REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.fragment_cam);
        intent=getIntent();

        cameraFragment = CameraFragment.this;

        btnCapture = (Button) findViewById(R.id.btnCapture);
        textureView = (TextureView) findViewById(R.id.cameraTextureView);

        cameraPreview = new CameraPreview(this, textureView, btnCapture);
        cameraPreview.setOnCallbackListener(this);

        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storagePermission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CameraFragment.REQUEST_STORAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CAM:
                for (int i = 0 ; i < permissions.length ; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)){
                        if (grantResult == PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "Preview Set !!!");
                            cameraPreview.openCamera();
                        } else {
                            Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
            case REQUEST_STORAGE:
                for (int i = 0 ; i < permissions.length ; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        if (grantResult == PackageManager.PERMISSION_GRANTED){
                            cameraPreview.openCamera();
                            Log.d(TAG, "Preview Set !!!");
                        } else{
                            Toast.makeText(this, "저장소 권한이 필요합니다", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.onPause();
    }

    @Override
    public void onSave(File filePath){
        Log.d(TAG, "onSave");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(filePath));
        sendBroadcast(intent);
    }

}