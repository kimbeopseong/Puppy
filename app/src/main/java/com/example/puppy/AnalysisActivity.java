package com.example.puppy;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnalysisActivity extends AppCompatActivity {
// Result Activity와 동일한 화면 구성이지만, 얘는 분석 후, 결과를 DB에 기록하는 역할로 제작.

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference poopData;
    ImageView picture;
    TextView date, stat, lv;
    Date currentDate;
    String an_date, an_stat, an_lv, dataId;
    final String TAG = "Analysis Activity";
    final String date_TAG = "date";
    final String stat_TAG = "stat";
    final String lv_TAG = "lv";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        poopData = db.collection("PoopData");

        long now = System.currentTimeMillis();
        currentDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dataIdForm = new SimpleDateFormat("yyyyMMddhhmmss");
        dataId = dataIdForm.format(currentDate);
        an_date = simpleDateFormat.format(currentDate);
        an_stat = "this is stat";
        an_lv = "2";

        Map<String, Object> dataset = new HashMap<>();
        dataset.put(date_TAG, an_date);
        dataset.put(stat_TAG, an_stat);
        dataset.put(lv_TAG, an_lv);
        poopData.document(dataId).set(dataset).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Document Snapshot successfully written!!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing Document", e);
            }
        });

        picture = (ImageView) findViewById(R.id.res_pic);
        date = (TextView) findViewById(R.id.res_date);
        stat = (TextView) findViewById(R.id.res_stat);
        lv = (TextView) findViewById(R.id.res_lv);

        date.setText(an_date);
        stat.setText(an_stat);
        lv.setText(an_lv);

    }
}
