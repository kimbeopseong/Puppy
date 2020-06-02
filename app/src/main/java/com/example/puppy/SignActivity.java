package com.example.puppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Splitter;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import xyz.hasnat.sweettoast.SweetToast;

public class SignActivity extends AppCompatActivity {
    private static final String TAG = "SignActivity";
    private Button   btnSignup;
    private EditText etUserEmail, etUserPassword,etUserPasswordConfirm, etUserName;
    private TextView tvHavedAccount;


    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        mAuth=FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        btnSignup = (Button)findViewById(R.id.signup_button);
        etUserEmail = (EditText)findViewById(R.id.signup_email);
        etUserPassword = (EditText)findViewById(R.id.signup_password);
        etUserPasswordConfirm=(EditText)findViewById(R.id.signup_confirm);
        etUserName=(EditText)findViewById(R.id.signup_name);

        tvHavedAccount = (TextView)findViewById(R.id.already_have_account);
        progressDialog = new ProgressDialog(SignActivity.this);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUserEmail.getText().toString();
                String password = etUserPassword.getText().toString();
                String confirmPassword=etUserPasswordConfirm.getText().toString();
                String name=etUserName.getText().toString();
                CreateNewAccount(email, password, confirmPassword, name);
            }
        });

        tvHavedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

    }

    private void CreateNewAccount(String email, String password, String confirmPassword, final String name) {


        if (TextUtils.isEmpty(email)) {
            SweetToast.error(SignActivity.this, "Your email is required.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetToast.error(SignActivity.this, "Your email is not valid.");
        } else if (TextUtils.isEmpty(password)) {
            SweetToast.error(SignActivity.this, "Please fill this password field");
        } else if (password.length() < 6) {
            SweetToast.error(SignActivity.this, "Create a password at least 6 characters long.");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            SweetToast.warning(SignActivity.this, "Please retype in password field");
        } else if (!password.equals(confirmPassword)) {
            SweetToast.error(SignActivity.this, "Your password don't match with your confirm password");
        } else if (TextUtils.isEmpty(name)) {
            SweetToast.warning(SignActivity.this, "Please write your name");
        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> Userinfo = new HashMap<>();
                                String currentUserID = mAuth.getCurrentUser().getUid();

                                Userinfo.put("name", name);
                                Userinfo.put("verified", "false");

                                db.collection("Users").document(currentUserID).set(Userinfo, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                    currentUser = mAuth.getCurrentUser();
                                                    if (currentUser != null) {
                                                        currentUser.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            registerSuccessPopUp();

                                                                            // LAUNCH activity after certain time period
                                                                            new Timer().schedule(new TimerTask() {
                                                                                public void run() {
                                                                                    SignActivity.this.runOnUiThread(new Runnable() {
                                                                                        public void run() {
                                                                                            mAuth.signOut();

                                                                                            Intent mainIntent = new Intent(SignActivity.this, LoginActivity.class);
                                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(mainIntent);
                                                                                            finish();

                                                                                            SweetToast.info(SignActivity.this, "Please check your email & verify.");

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }, 8000);


                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }

                                            }
                                        });

                            } else {
                                String message = task.getException().getMessage();
                                SweetToast.error(SignActivity.this, "Error occurred : " + message);
                            }
                            progressDialog.dismiss();
                        }
                    });
            //config progressbar
            progressDialog.setTitle("Creating new account");
            progressDialog.setMessage("Please wait a moment....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }
    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(SignActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignActivity.this);
        View view = LayoutInflater.from(SignActivity.this).inflate(R.layout.register_success_popup, null);

        //ImageButton imageButton = view.findViewById(R.id.successIcon);
        //imageButton.setImageResource(R.drawable.logout);
        builder.setCancelable(false);

        builder.setView(view);
        builder.show();
    }
}
