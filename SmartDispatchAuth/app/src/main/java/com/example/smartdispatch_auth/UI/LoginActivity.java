package com.example.smartdispatch_auth.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdispatch_auth.R;
import com.example.smartdispatch_auth.UI.Admin.AdminMainActivity;
import com.example.smartdispatch_auth.UI.Hospital.HospitalMainActivity;
import com.example.smartdispatch_auth.UI.Requester.RequesterMainActivity;
import com.example.smartdispatch_auth.UI.Vehicle.VehicleMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private ProgressDialog progress;

    private EditText mEmail, mPassword;

    // vars
    private String authenticator;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(this);
        progress.setMessage("Logging in...");
        progress.setCancelable(false);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        Intent intent = getIntent();
        authenticator = intent.getStringExtra("authenticator");
        if(authenticator.equals("admin"))
            findViewById(R.id.link_register).setVisibility(View.GONE);

        setupFirebaseAuth();
        findViewById(R.id.email_log_in_button).setOnClickListener(this);
        findViewById(R.id.link_register).setOnClickListener(this);

        TextView textView = findViewById(R.id.loginType);

        if(authenticator.equals("requester")) {
            textView.setText("LOGIN AS A REQUESTER");
        }
        else if(authenticator.equals("vehicle")){
            textView.setText("LOGIN AS A VEHICLE");
        }
        else if(authenticator.equals("hospital")){
            textView.setText("LOGIN AS A HOSPITAL");
        }
        else
            textView.setText("LOGIN AS AN ADMIN");
    }

    /* Override Methods */

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_register: {
                if(authenticator.equals("requester")) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("user", "requester");
                    startActivity(intent);
                }
                else if(authenticator.equals("hospital")){
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("user", "hospital");
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("user", "vehicle");
                    startActivity(intent);
                }
                break;
            }

            case R.id.email_log_in_button: {
                signIn();
                break;
            }
        }
    }

    /* Helper Methods */

    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "State Changed!");
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    switch (authenticator){
                        case "requester": {

                            Map<String, Object> token = new HashMap<>();
                            token.put("token", FirebaseInstanceId.getInstance().getToken());
                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                                FirebaseFirestore.getInstance().collection(getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token, SetOptions.merge());

                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("type", "requester");
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, RequesterMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case "vehicle" :{

                            Map<String, Object> token = new HashMap<>();
                            token.put("token", FirebaseInstanceId.getInstance().getToken());
                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                                FirebaseFirestore.getInstance().collection(getString(R.string.collection_vehicles))
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token, SetOptions.merge());

                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("type", "vehicle");
                            editor.apply();


                            Intent intent = new Intent(LoginActivity.this, VehicleMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case "hospital" :{
                            Map<String, Object> token = new HashMap<>();
                            token.put("token", FirebaseInstanceId.getInstance().getToken());
                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                                FirebaseFirestore.getInstance().collection(getString(R.string.collection_hospitals))
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token, SetOptions.merge());

                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("type", "hospital");
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, HospitalMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case "admin" :{
                            Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public void signIn() {
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        if (!isEmpty(email) && !isEmpty(password)) {

            progress.show();


            boolean check = true;
            if(!authenticator.equals("requester")){
                if(!email.contains("@smartdispatch.gov.in")){
                    check = false;
                }else{
                    if(authenticator.equals("vehicle") && !email.contains("v_"))
                        check = false;
                    else if(authenticator.equals("hospital") && !email.contains("h_"))
                        check = false;
                }
            }

            if(authenticator.equals("admin"))
                check = true;

            if(!check){
                Toast.makeText(LoginActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                progress.hide();
                return;
            }



            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginActivity", "signInWithEmail:success");

                            } else if (task.getException() != null) {
                                // If sign in fails, display a message to the user.

                                Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(LoginActivity.this, "Authentication failed. ", Toast.LENGTH_SHORT).show();
                            }
                            progress.dismiss();
                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }
}
