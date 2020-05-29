package com.example.puppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddCatActivity extends AppCompatActivity {
    EditText etCatName;
    EditText etCatAge;
    EditText etCatSpecies;
    RadioButton rbtnCMale;
    RadioButton rbtnCFemale;
    Button btnAddCat;
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
                Intent intent = new Intent(AddCatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
