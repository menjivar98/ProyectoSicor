package com.fmenjivar.sicor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class SetupActivity extends AppCompatActivity {

    Toolbar setupToolbar;
    private CircleImageView setupImage;
    private  Uri mainImageURI = null;
    private EditText setupName;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private boolean isCHanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");


        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_text);
        setupBtn = findViewById(R.id.button_setup);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

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

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


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

    }



    private void checkAndRequesForPermission() {

        if (ContextCompat.checkSelfPermission(SetupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {

            BringImagePicker();
        }


    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isCHanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setupUser(final String userName) {

         user_id = firebaseAuth.getCurrentUser().getUid();

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
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else {
                    showMessageErro(task.getException().getMessage());
                }




            }
        });

    }


    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT).show();

    }

    private void showMessageErro(String text){
        Toast.makeText(getApplicationContext(),"Error:" + text,Toast.LENGTH_SHORT).show();
    }
}
