package com.example.puppy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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


public class ResultActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUID;
    private String itemId = null;

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

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        db = FirebaseFirestore.getInstance();
        poopData = db.collection("PoopData");
        docRef = poopData.document(itemId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        res_date.setText(document.get("date").toString());
                        res_stat.setText(document.get("stat").toString());
                        res_lv.setText(document.get("lv").toString());
                    }
                }
            }
        });
//      기록할 때, 고양이의 이름은 따로 클래스에 저장되도록 해서 쿼리에서 이름 불러올 수 있도록 하기.
//        query = poopData.whereEqualTo("UID",currentUID).whereEqualTo("p_name", "콩순이");
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult()){
//                        res_date.setText(document.get("date").toString());
//                        res_stat.setText(document.get("stat").toString());
//                        res_lv.setText(document.get("lv").toString());
//                    }
//                }
//            }
//        });

        Button btn_resConfirm = (Button) findViewById(R.id.res_btn_confirm);
        btn_resConfirm.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }));

    }
}
