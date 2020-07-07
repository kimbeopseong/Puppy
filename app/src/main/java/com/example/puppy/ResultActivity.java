package com.example.puppy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puppy.ui.camera.CameraFragment;
import com.example.puppy.ui.list.RecordListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ResultActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUID;
    private String currentPID;
    private String itemId = null;
    private String date;

    FirebaseFirestore db;
    ImageView res_pic;
    TextView res_date, res_stat, res_lv;
    CollectionReference poopData;
    DocumentReference docRef;
    Query query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        res_pic = (ImageView) findViewById(R.id.res_pic);
        res_date = (TextView) findViewById(R.id.res_date);
        res_stat = (TextView) findViewById(R.id.res_stat);
        res_lv = (TextView) findViewById(R.id.res_lv);

        Button btn_resConfirm = (Button) findViewById(R.id.res_btn_confirm);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        db = FirebaseFirestore.getInstance();
        currentPID = intent.getStringExtra("pid");
        poopData = db.collection("Pet").document(currentPID).collection("PoopData");
        try {
            itemId = intent.getStringExtra("itemId");

            docRef = poopData.document(itemId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            final String uri = document.get("poopy_uri").toString();
                            Picasso.get().load(uri)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0, 70)
                                    .into(res_pic, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(uri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,70)
                                                    .into(res_pic);
                                        }
                                    });
                            res_date.setText(document.get("date").toString());
                            res_stat.setText(document.get("stat").toString());
                            res_lv.setText(document.get("lv").toString());
                        }
                    }
                }
            });

            btn_resConfirm.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraFragment cameraFragment = (CameraFragment) CameraFragment.cameraFragment;
                    cameraFragment.finish();
                    finish();
                }
            }));
        } catch (Exception e){
            date = intent.getStringExtra("date");

            poopData.whereEqualTo("date",date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            final String uri = document.get("poopy_uri").toString();
                            Picasso.get().load(uri)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile_image)
                            .error(R.drawable.default_profile_image)
                            .resize(0, 200)
                            .into(res_pic, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri)
                                            .placeholder(R.drawable.default_profile_image)
                                            .error(R.drawable.default_profile_image)
                                            .resize(0,200)
                                            .into(res_pic);
                                }
                            });
                            res_date.setText(document.get("date").toString());
                            res_stat.setText(document.get("stat").toString());
                            res_lv.setText(document.get("lv").toString());
                        }
                    }
                }
            });
            btn_resConfirm.setOnClickListener((new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finish();
                }
            }));

        }

    }
}
