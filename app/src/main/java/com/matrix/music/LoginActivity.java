package com.matrix.music;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText txtlogin, txtpassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    LinearLayout linearSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        linearSignup = findViewById(R.id.linear_signup);


        TextView forgettv =findViewById(R.id.forgettv);
        forgettv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPassword.class);
                 startActivity(intent);
            }
        });

        TextView tvsignup = findViewById(R.id.signup);
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        txtlogin = findViewById(R.id.email);
        txtpassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtlogin.getText().toString();
                String password = txtpassword.getText().toString();
               if(email.isEmpty()){
                   txtlogin.setError("Ente Email");
                   return;
               }
               if(password.isEmpty()){
                   txtpassword.setError("Enter Password");
                   return;
               }

                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Please Wait!!");
                progressDialog.setTitle("Checking Account!");
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                       progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                        }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,
                                "Please Check Your login Credentials",
                                Toast.LENGTH_SHORT).show();
                    }


                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
                String uid = mAuth.getCurrentUser().getUid();

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }
    }
}