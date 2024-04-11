package com.matrix.music;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    EditText editText;
    Button btn;
    private FirebaseAuth mAuth; //firebase authentiacation instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().hide();
        editText = findViewById(R.id.email);
        if(getIntent().getStringExtra("email")!=null){
            editText.setText(getIntent().getStringExtra("email"));
        }
        btn = findViewById(R.id.reset);
        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editText.getText().toString().trim();
                if(email.isEmpty()){
                    editText.setError("Enter Email");
                }
                else {
                    beginRecovery(email);
                }

            }
        });
    }
    private void beginRecovery(String email) {

// send recovery email to email id through firebase
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(ForgetPassword.this,"A link is sent on your given email",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgetPassword.this,"Error Occurred",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 }
        });
    }
}