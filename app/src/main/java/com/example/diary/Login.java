package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener{
    TextInputEditText editTextEmail, ediTextPassword;
    TextView btnTxtSignUp;
    ImageView buttonLogGoogle;
    Button buttonLog;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    Context context;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
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

        buttonLog.setOnClickListener(this);
        buttonLogGoogle.setOnClickListener(this);
        btnTxtSignUp.setOnClickListener(this);
    }
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Authentication success", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                }
        );
    }
    public void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onClick(View view){
        progressBar.setVisibility(View.VISIBLE);
        switch (view.getId()){
            case R.id.btn_login:
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(ediTextPassword.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(context, "Email empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Password empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                signIn(email,password);
                break;
            case R.id.SignUp:
                Intent intentCheckIfLogIn = new Intent(context, Register.class);
                startActivity(intentCheckIfLogIn);
                progressBar.setVisibility(View.GONE);
                break;
            case R.id.btn_loginGoogle:
                progressBar.setVisibility(View.GONE);
                break;
            default:
        }
    }
}