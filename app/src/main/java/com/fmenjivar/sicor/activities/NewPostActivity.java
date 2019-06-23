package com.fmenjivar.sicor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fmenjivar.sicor.MainActivity;
import com.fmenjivar.sicor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class NewPostActivity extends AppCompatActivity {

    ImageView newPostImage;

    EditText newPostTitle;
    EditText newPostDesc;
    Chip chipLow,chipMedium, chipHigh;
    Button newPostBtn;
    ProgressBar progressBar;
    String opc;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private Uri postImageUri = null;

    private String current_user_id;

    Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostImage = findViewById(R.id.image_post);
        newPostTitle = findViewById(R.id.et_post_title);
        newPostDesc = findViewById(R.id.et_post_desc);
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

        chipLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { opc = "Low"; }
        });

        chipMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { opc = "Medium"; }
        });


        chipHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { opc = "High"; }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title = newPostTitle.getText().toString();
                final String desc = newPostDesc.getText().toString();

                if(!title.isEmpty() && !desc.isEmpty() && postImageUri != null){
                    progressBar.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    final StorageReference filePath = storageReference.child("post_image").child(randomName + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                File actualImageFile= new File(postImageUri.getPath());

                                try {
                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(200)
                                            .setMaxWidth(200)
                                            .compressToBitmap(actualImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                byte[] thumbData = baos.toByteArray();

                                final UploadTask uploadTask = storageReference.child("post_image/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        /*************************************************************/
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                Map<String,Object> postMap = new HashMap<>();
                                                postMap.put("image_url",uri.toString());
                                                postMap.put("title",title);
                                                postMap.put("description",desc);
                                                postMap.put("image_thumb",uploadTask.toString());
                                                postMap.put("user_id",current_user_id);
                                                postMap.put("danger",opc);
                                                postMap.put("timestamp",FieldValue.serverTimestamp());

                                                firebaseFirestore.collection("Post").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        if(task.isSuccessful()){
                                                            showMessage("The Post was added");
                                                            Intent maintIntent = new Intent(NewPostActivity.this,MainActivity.class);
                                                            startActivity(maintIntent);
                                                            finish();
                                                        }/*else {

                                                        }
                                                        */
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }
                    });
                }else{
                    showMessage("Please fill out all required fields");
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
