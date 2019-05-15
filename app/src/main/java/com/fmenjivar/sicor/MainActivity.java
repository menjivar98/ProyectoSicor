package com.fmenjivar.sicor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fmenjivar.sicor.Activities.LoginActivity;
import com.fmenjivar.sicor.Activities.SetupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private  FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("SICOR");

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Primer paso
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){

            sendtoLogin();
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
}
