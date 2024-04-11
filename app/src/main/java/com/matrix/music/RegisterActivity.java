package com.matrix.music;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    ImageView imgback;
    EditText regnam, regpass, regemail;
    Button btnRegister;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        imgback = findViewById(R.id.back);
        regnam = findViewById(R.id.name);
        regpass = findViewById(R.id.password);
        regemail = findViewById(R.id.email);
        btnRegister = findViewById(R.id.register);
        mAuth=FirebaseAuth.getInstance();
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = regnam.getText().toString();
                String email = regemail.getText().toString();
                String password = regpass.getText().toString();
                if(name.isEmpty()){
                    regnam.setError("Enter Your Name");
                    return;
                }
                if(email.isEmpty()){
                    regemail.setError("Enter Your Email");
                    return;
                }
                if(password.isEmpty()){
                    regpass.setError("Enter Your Password");
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Creating Account!");
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            String uid = FirebaseAuth.getInstance().getUid();
                            HashMap<String,String> map = new HashMap<>();
                            map.put("name",name);
                            map.put("email",email);
                            map.put("password",password);
                            FirebaseDatabase.getInstance().getReference().child("user").child(uid).setValue(map);
                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"You are not Registered! Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}