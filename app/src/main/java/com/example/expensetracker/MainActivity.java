package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPass;
    private Button login_btn;
    private TextView mForgetPass;
    private TextView mSignUp;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    Boolean flag = false;

    Boolean validate(String email, String pass){
        if(email.length() < 6 || email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pass.length() < 6 || pass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDialog.setMessage("Processing..");
        mDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    flag = true;
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    Toast.makeText(getApplicationContext(), "Login successful ", Toast.LENGTH_SHORT).show();

                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
                Log.d("Main Activity", "From onComplete "+ flag);

            }
        });
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        login_btn = findViewById(R.id.button_login);
        mForgetPass = findViewById(R.id.forget_password);
        mSignUp = findViewById(R.id.signup_registration);
        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                Boolean out = validate(email, pass);
                Log.d("Main Activity", "Return from validate "+out);

            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });

        mForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Reset.class));
            }
        });
    }
}