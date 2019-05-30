package com.fmenjivar.sicor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fmenjivar.sicor.activities.LoginActivity;
import com.fmenjivar.sicor.activities.NewPostActivity;
import com.fmenjivar.sicor.activities.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private  FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



        addPostBtn =  findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPostIntent= new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Primer paso
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){

            sendtoLogin();
        }else {
            current_user_id = mAuth.getCurrentUser().getUid();
            firestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        if (! task.getResult().exists()){

                            Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }
                    }else {

                        showMessage(task.getException().getMessage());

                    }

                }
            });

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        
        switch (item.getItemId()){
            case R.id.action_logout_btn: 
                logOut();
                return true;
            case R.id.action_setting_btn:
                Intent settingIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingIntent);

        }
        
        
        return true;
    }

    private void logOut() {

        mAuth.signOut();
        sendtoLogin();

    }

    private void sendtoLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),"Error:" + text,Toast.LENGTH_SHORT).show();

    }
}
