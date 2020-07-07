package com.example.puppy.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.puppy.MainActivity;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import xyz.hasnat.sweettoast.SweetToast;

public class CameraFragment extends AppCompatActivity {
    int REQUEST_IMAGE_CODE=1001;
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private StorageReference mStorageRef;
    private String currentUserID;
    private FirebaseAuth mAuth;

    private String imageFilePath;
    private Uri photoUri;

    FirebaseFirestore db;
    Intent intent;

    String poopy_uri;
    private String date, stat, lv, currentPID;

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
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = simpleDateFormat.format(mDate);

        intent=getIntent();
        currentPID = intent.getStringExtra("pid");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE){
            final Uri image=data.getData();

            final StorageReference riversRef = mStorageRef.child("Feeds").child(currentUserID).child(intent.getExtras().get("pid").toString()).child("poopy.jpg");
            UploadTask uploadTask=riversRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(CameraFragment.this, "Poopy Photo Error: " + task.getException().getMessage());
                    }
                    poopy_uri=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        poopy_uri=task.getResult().toString();
                        stat = "this is stat";
                        lv = "1";

                        final HashMap<String, Object> update_poopy_data=new HashMap<>();
                        update_poopy_data.put("poopy_uri",poopy_uri);
                        update_poopy_data.put("uid",currentUserID);
                        update_poopy_data.put("date",date);
                        update_poopy_data.put("stat",stat);
                        update_poopy_data.put("lv",lv);


                        db.collection("Pet").document(intent.getExtras().get("pid").toString()).collection("PoopData").document().set(update_poopy_data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent goResult = callResult(update_poopy_data);
                                        startActivity(goResult);
                                        finish();
                                    }
                                });
                    }
                }
            });

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final StorageReference riversRef = mStorageRef.child("Feeds").child(currentUserID).child(intent.getExtras().get("pid").toString()).child("poopy.jpg");
            UploadTask uploadTask=riversRef.putFile(photoUri);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(CameraFragment.this, "Poopy Photo Error: " + task.getException().getMessage());
                    }
                    poopy_uri=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        poopy_uri=task.getResult().toString();
                        stat = "this is stat";
                        lv = "1";

                        final HashMap<String, Object> update_poopy_data=new HashMap<>();
                        update_poopy_data.put("poopy_uri",poopy_uri);
                        update_poopy_data.put("uid",currentUserID);
                        update_poopy_data.put("date",date);
                        update_poopy_data.put("stat",stat);
                        update_poopy_data.put("lv",lv);

                        db.collection("Pet").document(intent.getExtras().get("pid").toString()).collection("PoopData").document().set(update_poopy_data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent goResult = callResult(update_poopy_data);
                                        startActivity(goResult);
                                        finish();
                                    }
                                });
                    }
                }
            });
        }
    }

    private Intent callResult(HashMap<String, Object> map){
        Intent result = new Intent(this, ResultActivity.class);
        result.putExtra("uri", poopy_uri);
        result.putExtra("date",date);
        result.putExtra("pid", currentPID);
        return result;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "poopy";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

}