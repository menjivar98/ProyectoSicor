package com.fmenjivar.sicor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fmenjivar.sicor.activities.LoginActivity;
import com.fmenjivar.sicor.activities.NewPostActivity;
import com.fmenjivar.sicor.activities.SetupActivity;
import com.fmenjivar.sicor.fragment.AccountFragment;
import com.fmenjivar.sicor.fragment.HomeFragment;
import com.fmenjivar.sicor.fragment.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    BottomNavigationView mainbottomNav;


    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mainbottomNav = findViewById(R.id.mainBottomNav);


        //Fragments
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();

        replaceFragment(homeFragment);

        if(mAuth.getCurrentUser() != null){
            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()){

                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true;
                        case R.id.bottom_action_notificaion:
                            replaceFragment(notificationFragment);
                            return true;
                        case R.id.bottom_action_account:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }

                }
            });

            addPostBtn =  findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent= new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);


                }
            });
        }

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

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_cointer,fragment);
        fragmentTransaction.commit();

    }

}
