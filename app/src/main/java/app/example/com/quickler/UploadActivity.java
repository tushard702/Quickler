package app.example.com.quickler;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UploadActivity extends AppCompatActivity {

    private RadioGroup typeSelector;
    private LinearLayout textLayout, imageLayout;
    private Button textSubmit, imageSubmit, selectImage;
    private TextInputLayout textInputStatus, captionInput;
    private ImageView selectedImage;
    private Uri imageUri;
    //Firebase Variables
    DatabaseReference mPostsRef;

    private final static int GALLERY_REQ = 007;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        typeSelector = findViewById(R.id.upload_RadioGroup);
        textLayout = findViewById(R.id.text_input_layout);
        imageLayout = findViewById(R.id.image_upload_layout);
        textInputStatus = findViewById(R.id.text_inputStatus);
        captionInput = findViewById(R.id.captionInput);
        textSubmit = findViewById(R.id.text_uploadBtn);
        imageSubmit = findViewById(R.id.image_uploadBtn);
        selectImage = findViewById(R.id.imageSelector);
        selectedImage = findViewById(R.id.gallerySelectedImage);

        mPostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").push();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 001);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        typeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.text_upload_RadioBtn :
                        imageLayout.setVisibility(View.GONE);
                        textLayout.setVisibility(View.VISIBLE);
                        break;

                    case R.id.image_upload_RadioBtn :
                        textLayout.setVisibility(View.GONE);
                        imageLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        textSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mProgressDialog = new ProgressDialog(UploadActivity.this);
            mProgressDialog.setTitle("Creating Post");
            mProgressDialog.setMessage("Please wait while we write data in out database.......");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            String status = textInputStatus.getEditText().getText().toString();
            if(!status.equals("")){
                mPostsRef.child("name").setValue(status);
                mPostsRef.child("likes").setValue(0);
                mPostsRef.child("type").setValue("text").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Post Created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else{
                mProgressDialog.hide();
                Toast.makeText(getApplicationContext(), "There is an error, Please try again", Toast.LENGTH_SHORT).show();
            }

            }
        });

        imageSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    String caption = captionInput.getEditText().getText().toString();
                    Log.d("DATA", caption);
                    Log.d("DATA", ""+imageUri);
                    if(!caption.equals("")){
                        Toast.makeText(getApplicationContext(), caption, Toast.LENGTH_SHORT).show();
                        //Upload Image and caption in Database
                        mProgressDialog = new ProgressDialog(UploadActivity.this);
                        mProgressDialog.setTitle("Uploading Image");
                        mProgressDialog.setMessage("Please wait while we upload image....");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();

                        mPostsRef.child("name").setValue(caption).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String postID =  mPostsRef.getKey();
                                    final StorageReference postStorage = FirebaseStorage.getInstance().getReference().child("Post Images").child(postID+".jpg");

                                    UploadTask uploadTask = postStorage.putFile(imageUri);
                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if(!task.isSuccessful()){
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Please try again, an error occured", Toast.LENGTH_SHORT).show();
                                                throw task.getException();
                                            }

                                            return postStorage.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                Uri downloadUrl = task.getResult();
                                                mPostsRef.child("image").setValue(downloadUrl.toString());
                                                mPostsRef.child("type").setValue("image");
                                                mPostsRef.child("likes").setValue(0);
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Post created Successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else{
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Please try again, an error occured", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(getApplicationContext(), "Please try again, an error occured", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            }
                        });



                    }else{
                        Toast.makeText(getApplicationContext(), "Write a caption", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please Select a Image to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(selectedImage);
            selectedImage.setVisibility(View.VISIBLE);
        }
    }

}
