package com.example.puppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCatActivity extends AppCompatActivity {
    EditText etCatName;
    EditText etCatAge;
    EditText etCatSpecies;
    RadioButton rbtnCMale;
    RadioButton rbtnCFemale;
    Button btnAddCat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddCatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);

        etCatName = (EditText)findViewById(R.id.etCatName);
        etCatAge = (EditText)findViewById(R.id.etCatAge);
        etCatSpecies = (EditText)findViewById(R.id.etCatSpecies);
        rbtnCMale = (RadioButton)findViewById(R.id.rbtnCMale);
        rbtnCFemale = (RadioButton)findViewById(R.id.rbtnCFemale);
        btnAddCat = (Button)findViewById(R.id.btnAddCat);

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user = new HashMap<>();
                user.put("first", "Ada");
                user.put("last", "Lovelace");
                user.put("born", 1815);

                // Add a new document with a generated ID
                db.collection("Cat")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                Intent intent = new Intent(AddCatActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
