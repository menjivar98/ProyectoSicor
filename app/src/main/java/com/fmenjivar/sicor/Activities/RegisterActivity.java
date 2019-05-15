package com.fmenjivar.sicor.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText remail, rpassword,rconfirmpass;
    private Button reg_btn,reg_login_btn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        remail = findViewById(R.id.reg_email);
        rpassword = findViewById(R.id.password_reg);
        rconfirmpass = findViewById(R.id.password_reg_2);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.btn_reg_back);
        progressBar = findViewById(R.id.register_progress);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = remail.getText().toString();
                String pass = rpassword.getText().toString();
                String confirm_pass = rconfirmpass.getText().toString();

                if(email.isEmpty()  ||  pass.isEmpty()){
                    showMessage("Please Verify all Field");
                    progressBar.setVisibility(View.INVISIBLE);
                    reg_btn.setVisibility(View.VISIBLE);

                }else if (!pass.equals(confirm_pass)){
                    showMessage("Your password is not equals");
                }else{
                    createUser(email,pass);
                }


            }
        });


        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }



    @Override
    protected void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }


        super.onStart();
    }

    private void sendToMain() {

        Intent maintIntent = new Intent(this, MainActivity.class);
        startActivity(maintIntent);
        finish();


    }


    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),"Error:" + text,Toast.LENGTH_SHORT).show();

    }

    private void createUser(String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    reg_btn.setVisibility(View.INVISIBLE);

                    sendToSetup();
                }else {
                    showMessage(task.getException().getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                    reg_btn.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void sendToSetup() {

        Intent setupIntent = new Intent(getApplicationContext(),SetupActivity.class);
        startActivity(setupIntent);
        finish();
    }

}
