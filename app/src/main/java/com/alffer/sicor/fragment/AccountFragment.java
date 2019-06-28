package com.alffer.sicor.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alffer.sicor.R;
import com.alffer.sicor.activities.LoginActivity;
import com.alffer.sicor.activities.SetupActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton addPost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);


        addPost = getActivity().findViewById(R.id.add_post_btn);
        addPost.hide();

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