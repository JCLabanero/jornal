package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, ediTextPassword;
    TextView btnTxtSignUp;
    ImageView buttonLogGoogle;
    Button buttonLog;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    Context context;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    @Override
    public void onStart() {
        super.onStart();
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
        context = this;
        editTextEmail = findViewById(R.id.email_login);
        ediTextPassword = findViewById(R.id.password_login);
        buttonLog = findViewById(R.id.btn_login);
        buttonLogGoogle = findViewById(R.id.btn_loginGoogle);
        progressBar = findViewById(R.id.progress_horizontal_1);
        btnTxtSignUp = findViewById(R.id.SignUp);
        mAuth = FirebaseAuth.getInstance();


        oneTapClient = Identity.getSignInClient(context);
        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
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
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(ediTextPassword.getText());
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
                                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if(idToken!=null){
                            String email = credential.getId();
//                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            Toast.makeText(Login.this, "."+email, Toast.LENGTH_SHORT).show();
//                            mAuth.signInWithCredential(firebaseCredential)
//                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                            if (task.isSuccessful()) {
//                                                // Sign in success, update UI with the signed-in user's information
//                                                Toast.makeText(Login.this, "Email: " + email, Toast.LENGTH_SHORT).show();
//                                                FirebaseUser user = mAuth.getCurrentUser();
//                                            } else {
//                                                // If sign in fails, display a message to the user.
//                                                Toast.makeText(Login.this, "Failed to authenticate!", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonLogGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneTapClient.beginSignIn(signUpRequest)
                        .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                                activityResultLauncher.launch(intentSenderRequest);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", e.getLocalizedMessage());
                                Toast.makeText(context, "FAILED!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}