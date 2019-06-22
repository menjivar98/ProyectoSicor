package com.fmenjivar.sicor.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.activities.LoginActivity;
import com.fmenjivar.sicor.activities.SetupActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth mAuth;
    private Button buttonSettings;
    private Button buttonlogOut;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        v.findViewById(R.id.btnAccountSett).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getActivity(), SetupActivity.class);
                startActivity(settingIntent);
            }
        });

        v.findViewById(R.id.btnLogut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void logOut(){
        mAuth.signOut();
        sendtoLogin();
    }

    private void sendtoLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
    }

}