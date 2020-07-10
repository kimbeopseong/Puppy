package com.example.puppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class CatSetActivity extends AppCompatActivity {
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    private static final String TAG = "CatSetActivity";
    private EditText updateCatName;
    private EditText updateCatAge;
    private EditText updateCatSpecies;
    private RadioButton rbtnCMale;
    private RadioButton rbtnCFemale;
    private Button btn_updateCat;
    private CircleImageView cvUpdateCat;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    String petUri,petName,petSex,petAge,petSpec;
    String pet_profile_download_url;
    private String currentUserID;
    private Intent cat_intent;
    private String document_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_set);
        cat_intent = getIntent();
        document_id = cat_intent.getStringExtra("cat_document_id");

        updateCatName=(EditText)findViewById(R.id.etUpdateCatName);
        updateCatAge=(EditText)findViewById(R.id.etUpdateCatAge);
        updateCatSpecies=(EditText)findViewById(R.id.etUPdateCatSpecies);
        rbtnCMale=(RadioButton)findViewById(R.id.upRbtnCMale);
        rbtnCFemale=(RadioButton)findViewById(R.id.upRbtnCFemale);
        cvUpdateCat=(CircleImageView)findViewById(R.id.cvUpdateCat);
        btn_updateCat=(Button)findViewById(R.id.btnUpdateCat);

        if(ContextCompat.checkSelfPermission(CatSetActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(CatSetActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(CatSetActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{ }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //load pet info from firebase
        DocumentReference docRef = db.collection("Pet").document(document_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    petName=task.getResult().get("p_name").toString();
                    petSex=task.getResult().get("p_sex").toString();
                    petAge=task.getResult().get("p_age").toString();
                    petSpec=task.getResult().get("p_species").toString();
                    if(task.getResult().contains("p_uri")){
                        petUri=task.getResult().get("p_uri").toString();
                        Picasso.get().load(petUri)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .resize(0,150)
                                .into(cvUpdateCat, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(petUri)
                                                .placeholder(R.drawable.default_profile_image)
                                                .error(R.drawable.default_profile_image)
                                                .resize(0,90)
                                                .into(cvUpdateCat);
                                    }
                                });
                    }
                    updateCatName.setText(petName);
                    updateCatAge.setText(petAge);
                    updateCatSpecies.setText(petSpec);
                    pet_profile_download_url = petUri;
                    if(petSex== "수컷")
                        rbtnCMale.setChecked(true);
                    else
                        rbtnCFemale.setChecked(true);
                }
            }
        });
        //
        cvUpdateCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });
        //update cat info
        btn_updateCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catName = updateCatName.getText().toString();
                int catAge = parseInt(updateCatAge.getText().toString());
                String catSpecies = updateCatSpecies.getText().toString();
                String catSex=null;
                if(rbtnCMale.isChecked())
                    catSex="수컷";
                else if(rbtnCFemale.isChecked())
                    catSex="암컷";
                updateCat(catName, catAge, catSpecies, catSex, pet_profile_download_url);
                Intent end_update = new Intent(CatSetActivity.this, MainActivity.class);
                startActivity(end_update);
                finish();
            }
        });
    }
    private void updateCat(String catName, int catAge, String catSpecies, String catSex, String catPhoto){
        if (TextUtils.isEmpty(catName)) {
            SweetToast.error(CatSetActivity.this, "Your cat's name is required.");
        } else if (catAge == 0) {
            SweetToast.error(CatSetActivity.this, "Your cat's age is required.");
        } else if (catAge > 25) {
            SweetToast.error(CatSetActivity.this, "You fill in wrong age.");
        }else if (TextUtils.isEmpty(catSpecies)) {
            SweetToast.warning(CatSetActivity.this, "Your cat's Species is required.");
        } else if (TextUtils.isEmpty(catSex)) {
            SweetToast.warning(CatSetActivity.this, "Select your cat's sex");
        }
        else {

            Map<String, Object> update = new HashMap<>();
            update.put("p_name", catName);
            update.put("p_sex", catSex);
            update.put("p_species", catSpecies);
            update.put("p_age", catAge);
            update.put("p_ID",currentUserID);
            update.put("p_uri", pet_profile_download_url);

            // Add a new document with a generated ID
            db.collection("Pet")
                    .document(document_id).set(update,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }
    //change pet photo
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE){
            final Uri image=data.getData();
            Log.d(TAG, "onActivityResult: "+image);
            Picasso.get().load(image)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .resize(0,100)
                    .into(cvUpdateCat);

            final StorageReference riversRef = mStorageRef.child("Pets").child(currentUserID).child(document_id).child("profile.jpg");
            UploadTask uploadTask=riversRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(CatSetActivity.this, "Profile Photo Error: " + task.getException().getMessage());
                    }
                    pet_profile_download_url=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        pet_profile_download_url=task.getResult().toString();
                    }
                }
            });

        }
    }
}