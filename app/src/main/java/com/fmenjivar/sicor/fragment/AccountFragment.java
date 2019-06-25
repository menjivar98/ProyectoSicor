package com.fmenjivar.sicor.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.activities.LoginActivity;
import com.fmenjivar.sicor.activities.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }

    //private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private FloatingActionButton addPost;

    //Toolbar setupToolbar;
    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private EditText setupName;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private boolean isCHanged = false;





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isCHanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        user_id = mAuth.getCurrentUser().getUid();


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        setupImage = v.findViewById(R.id.setup_image);
        setupName = v.findViewById(R.id.setup_text);
        setupBtn = v.findViewById(R.id.button_setup);


        addPost = getActivity().findViewById(R.id.add_post_btn);
        addPost.hide();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile);

                        Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }


                }else{
                    showMessageErro(task.getException().getMessage());
                }
            }
        });






        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkAndRequesForPermission();
                }else{
                    BringImagePicker();
                }

            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();

                if(isCHanged){
                    if(!user_name.isEmpty() && mainImageURI != null){
                        setupUser(user_name);
                    }

                }else{

                    storeFirestone(null,user_name);
                }

            }
        });



        v.findViewById(R.id.tvLogut).setOnClickListener(new View.OnClickListener() {
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


    ///////////////////////////////////////////////////////////////////////////



    private void checkAndRequesForPermission() {

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {

            BringImagePicker();
        }


    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getActivity());

    }

    private void setupUser(final String userName) {

        user_id = mAuth.getCurrentUser().getUid();

        final StorageReference image_path = storageReference.child("profile_images").child(user_id + " .jpg");
        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        storeFirestone(uri,userName);


                    }
                });


            }
        });


    }



    private void storeFirestone(Uri uri, String userName) {

        Map<String, String> userMap = new HashMap<>();

        if(uri != null){

            userMap.put("name",userName);
            userMap.put("image",uri.toString());
        }else{

            userMap.put("image",mainImageURI.toString());

        }




        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    showMessage("The user setting are updated");
                    /*
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    */
                }else {
                    showMessageErro(task.getException().getMessage());
                }




            }
        });

    }

    private void showMessage(String text) {
        Toast.makeText(getActivity().getApplicationContext(), text,Toast.LENGTH_SHORT).show();

    }

    private void showMessageErro(String text){
        Toast.makeText(getActivity().getApplicationContext(),"Error:" + text,Toast.LENGTH_SHORT).show();
    }

}