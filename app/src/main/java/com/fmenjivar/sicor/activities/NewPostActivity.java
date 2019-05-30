package com.fmenjivar.sicor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    ImageView newPostImage;
    EditText newPostDesc;
    Chip chipLow,chipMedium, chipHigh;
    Button newPostBtn;
    ProgressBar progressBar;
    String opc,low,medium,high;
    String desc;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private Uri postImageUri = null;

    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostImage = findViewById(R.id.image_post);
        newPostDesc = findViewById(R.id.edit_post);
        chipLow = findViewById(R.id.chip_low);
        chipMedium = findViewById(R.id.chip_medium);
        chipHigh = findViewById(R.id.chip_high);
        newPostBtn = findViewById(R.id.post_btn);
        progressBar = findViewById(R.id.progress_bar_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        current_user_id = mAuth.getCurrentUser().getUid();

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BringImagePicker();
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if(!desc.isEmpty() && postImageUri != null){
                    progressBar.setVisibility(View.VISIBLE);

                    String randomName = FieldValue.serverTimestamp().toString();

                    final StorageReference filePath = storageReference.child("post_image").child(randomName + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> postMap = new HashMap<>();
                                        postMap.put("image_url",uri.toString());
                                        postMap.put("description",desc);
                                        postMap.put("user_id",current_user_id);
                                        postMap.put("Danger",opc);
                                        postMap.put("timeStamp",FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){

                                                    showMessage("Post was added");
                                                    Intent maintIntent = new Intent(NewPostActivity.this,MainActivity.class);
                                                    startActivity(maintIntent);
                                                    finish();


                                                }else {

                                                }

                                                progressBar.setVisibility(View.INVISIBLE);
                                            }



                                        });

                                    }
                                });
                            }

                        }
                    });



                }else{
                    showMessage("Please complete all the field");
                }



            }
        });

    }



    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setAspectRatio(1,1)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();

                newPostImage.setImageURI(postImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT).show();
    }




}
