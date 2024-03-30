package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button btn;
    ProgressBar progressBar;
    TextView create_account_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        create_account_txt = findViewById(R.id.create_account_text_view_btn);

        btn.setOnClickListener(v->loginuser());
        create_account_txt.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    private void loginuser() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        boolean isValidated = validateData(emailText,passwordText);
        if(!isValidated){
            return;
        }
        loginAccountFirebase(emailText,passwordText);

    }

    private void loginAccountFirebase(String emailText, String passwordText) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this , MainActivity.class));
                    }
                    else{
                        Utility.showToast(LoginActivity.this,"Email not verified , Please verify your email.");
                    }
                }
                else{
                    Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                }

            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            btn.setVisibility(View.GONE);

        }
        else{
            progressBar.setVisibility(View.GONE);
            btn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailText,String passwordText){
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Email is invalid");
            return false;
        }
        if(passwordText.length()<6){
            password.setError("password length is invalid");
            return false;
        }
        return true;
    }
}