package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private CircleImageView addImage;
    private ImageView camera;
    private EditText firstName;
    private EditText lastName;
    private EditText instituteName;
    private Button saveBtn;
    private Uri image;
    private String downloadurl = "";
    private String phoneNumber = "";
    private FirebaseAuth auth;

    private ProgressBar progressBar;
    private ProgressDialog loadingDialog, loadingDialog2;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();

        addImage = findViewById(R.id.profile_image);
        camera = findViewById(R.id.camera);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        instituteName = findViewById(R.id.institute);
        saveBtn = findViewById(R.id.save_button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        type = getIntent().getIntExtra("type", 0);


        loadingDialog = new ProgressDialog(this, R.style.dialogStyle);
        loadingDialog.setMessage("Saving...");
        loadingDialog.setCancelable(false);

        if (type == 1) {
            phoneNumber = getIntent().getStringExtra("phone");
            toolbar.setVisibility(View.INVISIBLE);
        } else {
            firstName.setText(getIntent().getStringExtra("firstName"));
            lastName.setText(getIntent().getStringExtra("lastName"));
            instituteName.setText(getIntent().getStringExtra("instituteName"));
            phoneNumber = getIntent().getStringExtra("phone");

            Glide.with(EditProfileActivity.this).load(MainActivity.decodedBytes).placeholder(R.drawable.profile1_home).into(addImage);
            toolbar.setVisibility(View.VISIBLE);
        }

        loadingDialog2 = new ProgressDialog(this, R.style.dialogStyle);
        loadingDialog2.setMessage("Saving...");
        loadingDialog2.setCancelable(true);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().isEmpty() || firstName.getText() == null) {
                    firstName.setError("Required");
                    return;
                }
                if (lastName.getText().toString().isEmpty() || lastName.getText() == null) {
                    lastName.setError("Required");
                    return;
                }
                if (instituteName.getText().toString().isEmpty() || instituteName.getText() == null) {
                    instituteName.setError("Required");
                    return;
                }
//                if (image == null) {
//                   // Toast.makeText(EditProfileActivity.this, "Please select category iamge", Toast.LENGTH_SHORT).show();
//                    uploadInfo();
//                }else
//                {
//                    uploadData();
//                }
                uploadInfo();
            }
        });


    }

    private void setImage() {
        Dexter.withActivity(EditProfileActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            selectCropImage();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.\"", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void selectCropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorAccent))
                .setActivityTitle("Profile Photo")
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .start(this);

    }

    private void uploadData() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference imageReferece = storageReference.child("users").child(image.getLastPathSegment());

        UploadTask uploadTask = imageReferece.putFile(image);

        loadingDialog2.show();

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    loadingDialog2.dismiss();

                    throw task.getException();
                }

                // Continue with the task to get the download URL

                return imageReferece.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadurl = task.getResult().toString();
                            loadingDialog2.dismiss();
                            uploadInfo();
                        } else {
                            loadingDialog2.dismiss();

                            Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    loadingDialog2.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image = result.getUri();
                Glide.with(EditProfileActivity.this).load(image).placeholder(R.drawable.profile1_home).into(addImage);
                addImage.setImageURI(image);

                //********************* ussing bitmap ***********************//
                Bitmap bitmap = null;
                Bitmap resized = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                    resized = Bitmap.createScaledBitmap(bitmap, 125, 125, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.PNG, 1, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                //Use your Base64 String as you wish
                MainActivity.url = Base64.encodeToString(byteArray, Base64.DEFAULT);
                getSharedPreferences("PhotoUrl",MODE_PRIVATE).edit().putString(""+auth.getCurrentUser().getUid(),MainActivity.url).apply();

                //********************* ussing bitmap ***********************//

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadInfo() {

        final Map<String, Object> map = new HashMap<>();

        map.put("firstname", firstName.getText().toString());
        map.put("lastName", lastName.getText().toString());
        map.put("instituteName", instituteName.getText().toString());
        map.put("phone", phoneNumber);

        //map.put("url", downloadurl);
        map.put("url", MainActivity.url);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName.getText().toString() + " " + lastName.getText().toString()).build();

        loadingDialog.show();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (type == 1) {
                                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            loadingDialog.dismiss();
                                            finish();
                                        } else {
                                            MainActivity.firstName = firstName.getText().toString();
                                            MainActivity.lastName = lastName.getText().toString();
                                            MainActivity.institute = instituteName.getText().toString();

                                            getSharedPreferences("FirstName",MODE_PRIVATE).edit().putString(""+auth.getCurrentUser().getUid(),MainActivity.firstName).apply();
                                            getSharedPreferences("LastName",MODE_PRIVATE).edit().putString(""+auth.getCurrentUser().getUid(),MainActivity.lastName).apply();
                                            getSharedPreferences("Institute",MODE_PRIVATE).edit().putString(""+auth.getCurrentUser().getUid(),MainActivity.institute).apply();

                                            loadingDialog.dismiss();
                                            finish();
                                        }

                                    } else {
                                        loadingDialog.dismiss();

                                        Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
