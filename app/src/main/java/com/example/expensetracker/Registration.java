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

public class Registration extends AppCompatActivity {
    private EditText mEmail, mPass;
    private Button reg_btn;
    private TextView sign_in;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private static final String TAG = Registration.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Log.i(TAG,"Inside on create method");
        mEmail = findViewById(R.id.email_registration);
        mPass = findViewById(R.id.password_registration);
        reg_btn = findViewById(R.id.button_registration);
        sign_in = findViewById(R.id.signin_registration);
        try {
            mAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
        Log.i(TAG,"initiating firebase instance");
        mDialog = new ProgressDialog(this);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Registration..");
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                Log.i(TAG,"Username - "+email);
                Log.i(TAG,"Password - "+pass);
                if(TextUtils.isEmpty(email)){
                    Log.e(TAG,"Email is Required");
                    Toast.makeText(getApplicationContext(), "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Log.e(TAG,"Password is required");
                    Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDialog.setMessage("Processing..");
                Log.i(TAG,"Processing..");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            Log.i(TAG,"Adding user details to database");
                            Log.i(TAG,"Registration Successful");
                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"Moving to Home Activity");
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }
                        else{
                            mDialog.dismiss();
                            Log.e(TAG,"Registration Failed username already exists");
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Move to sign in page");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}