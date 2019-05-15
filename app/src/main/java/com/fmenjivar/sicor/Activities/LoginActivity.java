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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.login_email);
        loginPassText = findViewById(R.id.password_login);
        loginBtn = findViewById(R.id.btn_login);
        loginRegBtn = findViewById(R.id.btn_reg);
        loginProgress = findViewById(R.id.login_progress);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                if (!loginEmail.isEmpty() && !loginPass.isEmpty()){
                    signIn(loginEmail,loginPass);

                }else{
                    showMessage("Please Verify all Field");
                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        //Esta linea del codigo es para ver si el usuario esta logiado y si no esta te regresa a loginActivity
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){

            sendToMain();

        }
    }

    private void signIn(String loginEmail, String loginPass) {

        mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginProgress.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.INVISIBLE);

                    sendToMain();


                }else{
                    showMessage(task.getException().toString());
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

    }

    private void sendToMain(){

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
