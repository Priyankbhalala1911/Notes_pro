package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText email,password,confirmPassword;
    Button btn;
    ProgressBar progressBar;
    TextView login_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirmPassword = findViewById(R.id.confirm_password_edit_text);
        btn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        login_txt = findViewById(R.id.login_text_view_btn);

        btn.setOnClickListener(v->createAccount());
        login_txt.setOnClickListener(v->startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));
    }

    private void createAccount() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();

        boolean isValidated = validateData(emailText,passwordText,confirmPasswordText);
        if(!isValidated){
            return;
        }
        createAccountFirebase(emailText,passwordText);
    }

    private void createAccountFirebase(String emailText, String passwordText) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            Utility.showToast(CreateAccountActivity.this,"Successfully create account,Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
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

    boolean validateData(String emailText,String passwordText,String confirmPasswordText){
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
                email.setError("Email is invalid");
                return false;
        }
        if(passwordText.length()<6){
            password.setError("password length is invalid");
            return false;
        }
        if(!passwordText.equals(confirmPasswordText)){
            confirmPassword.setError("password not matched");
            return false;
        }
        return true;
    }
}