package com.example.puppy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Button btn_resConfirm = (Button) findViewById(R.id.res_btn_confirm);
        btn_resConfirm.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*              기록기능 미포함                    */
                onBackPressed();
            }
        }));

    }
}
