package com.alffer.sicor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alffer.sicor.MainActivity;
import com.alffer.sicor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*

    Esta clase lo que hace es que la primera pantallas que le aperece al usuario y asi
    recibir los datos del usuario y asi poder procesarlos y ver si esta registrado en la
    base de datos y la base de datos esta creada con firebase


 */

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgress;


    //Esta linea de codigo donde declaramos que queremos utilizar firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Aqui inicializamos la variable para asi obtener una instancia
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

                /*
                    Aqui en el momento de dar click en el boton primero se extrae los datos y se verifican
                    que estos campos no esten vacios y datos recibidos estan guardados en la base de datos
                    entonces es cuando se pasa a la pantalla home
                 */

                if (!loginEmail.isEmpty() && !loginPass.isEmpty()){
                    signIn(loginEmail,loginPass);

                }else{
                    showMessage("Please Verify all Field");
                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }

            }
        });

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regist);
                finish();
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

    /***
     *
     * @param loginEmail  estos parametros son recibidos en el momento que el usuario le da click al boton y estos son comparados
     *                    para ver si estan registrado
     * @param loginPass
     */

    private void signIn(String loginEmail, String loginPass) {

        mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginProgress.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.INVISIBLE);

                    sendToMain();
                    finish();


                }else{
                    showMessage(task.getException().getMessage());
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),"Error:" + text,Toast.LENGTH_SHORT).show();

    }

    private void sendToMain(){

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
