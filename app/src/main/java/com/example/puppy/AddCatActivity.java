package com.example.puppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import xyz.hasnat.sweettoast.SweetToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class AddCatActivity extends AppCompatActivity {
    EditText etCatName;
    EditText etCatAge;
    EditText etCatSpecies;
    RadioButton rbtnCMale;
    RadioButton rbtnCFemale;
    Button btnAddCat;
    FirebaseFirestore db;

    private static final String TAG = "AddCatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cat);

        db = FirebaseFirestore.getInstance();

        etCatName = (EditText)findViewById(R.id.etCatName);
        etCatAge = (EditText)findViewById(R.id.etCatAge);
        etCatSpecies = (EditText)findViewById(R.id.etCatSpecies);
        rbtnCMale = (RadioButton)findViewById(R.id.rbtnCMale);
        rbtnCFemale = (RadioButton)findViewById(R.id.rbtnCFemale);
        btnAddCat = (Button)findViewById(R.id.btnAddCat);

        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catName = etCatName.getText().toString();
                int catAge = parseInt(etCatAge.getText().toString());
                String catSpecies = etCatSpecies.getText().toString();
                String catSex=null;
                if(rbtnCMale.isChecked())
                    catSex="수컷";
                else if(rbtnCFemale.isChecked())
                    catSex="암컷";
                createNewCat(catName, catAge, catSpecies, catSex);
                Intent intent = new Intent(AddCatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void createNewCat(String catName, int catAge ,String catSpecies, String catSex) {
        if (TextUtils.isEmpty(catName)) {
            SweetToast.error(AddCatActivity.this, "Your Cat's Name is required.");
        } else if (catAge == 0) {
            SweetToast.error(AddCatActivity.this, "Please fill this password field");
        } else if (TextUtils.isEmpty(catSpecies)) {
            SweetToast.warning(AddCatActivity.this, "Please retype in password field");
        } else if (TextUtils.isEmpty(catSex)) {
            SweetToast.warning(AddCatActivity.this, "Please write your name");
        } else {
            Map<String, Object> user = new HashMap<>();

            user.put("p_name", catName);
            user.put("p_sex", catSex);
            user.put("p_species", catSpecies);
            user.put("p_age", catAge);

            // Add a new document with a generated ID
            db.collection("Pet")
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
        }
    }
}
