package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, ediTextPassword;
    TextView btnTxtSignUp;
    ImageView buttonLogGoogle;
    Button buttonLog;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    Context context;
//    CredentialsClient mCredentialsClient;
//
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    @Override
    public void onStart() {
        super.onStart();
        init();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        String email, password;
        email = String.valueOf(editTextEmail.getText());
        password = String.valueOf(ediTextPassword.getText());
        btnTxtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCheckIfLogedIn = new Intent(context, Register.class);
                startActivity(intentCheckIfLogedIn);
                finish();
            }
        });
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(context, "Email empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(context, "Password empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               progressBar.setVisibility(View.GONE);
                               if(task.isSuccessful()){
                                   Toast.makeText(getApplicationContext(), "Authentication success", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                   startActivity(intent);
                                   finish();
                               }else{
                                   Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                               }
                           }
                       }
                );
            }
        });
        buttonLogGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                oneTapClient = Identity.getSignInClient(context);
                signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                // Your server's client ID, not your Android client ID.
                                .setServerClientId(getString(R.string.default_web_client_id))
                                // Only show accounts previously used to sign in.
                                .setFilterByAuthorizedAccounts(true)
                                .build())
                        .build();
            }
        });
    }
    private void init(){
        context = this;
        editTextEmail = findViewById(R.id.email_login);
        ediTextPassword = findViewById(R.id.password_login);
        buttonLog = findViewById(R.id.btn_login);
        buttonLogGoogle = findViewById(R.id.btn_loginGoogle);
        progressBar = findViewById(R.id.progress_horizontal_1);
        btnTxtSignUp = findViewById(R.id.SignUp);
        mAuth = FirebaseAuth.getInstance();
//        mCredentialsApiClient = Credentials.getClient(this);
    }
}