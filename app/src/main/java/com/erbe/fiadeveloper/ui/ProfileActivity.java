package com.erbe.fiadeveloper.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityProfileBinding;
import com.erbe.fiadeveloper.ui.coaching.DetailCoachActivity;
import com.erbe.fiadeveloper.util.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private ActivityProfileBinding mBinding;

    private static final String TAG = "Image";
    private static final int RC_CHOOSE_PHOTO = 101;
    private static final int RC_IMAGE_PERMS = 102;
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;

    private StorageReference mImageRef;

    private FirebaseFirestore db;

    private String imageUri;

    private CircleImageView cek;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        cek = findViewById(R.id.profileImage);

        // Firestore
        db = FirebaseFirestore.getInstance();

        mBinding.profileImage.setOnClickListener(this);
        mBinding.finish.setOnClickListener(this);
        mBinding.detail.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            DocumentReference docRef = db.collection("coach").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.getString("photo") != null) {
                            GlideApp.with(ProfileActivity.this)
                                    .load(document.getString("photo"))
                                    .centerCrop()
                                    .into(cek);
                        }
                        if (document.getString("topic") != null) {
                            mBinding.topic.setText(document.getString("topic"));
                        }
                        if (document.getString("description") != null) {
                            mBinding.description.setText(document.getString("description"));
                        }
                        mBinding.progressLoading.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        mBinding.progressLoading.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                uploadPhoto(selectedImage);
            } else {
                Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE
                && EasyPermissions.hasPermissions(this, PERMS)) {
            choosePhoto();
        }
    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    protected void choosePhoto() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rational_image_perm),
                    RC_IMAGE_PERMS, PERMS);
            return;
        }

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void uploadPhoto(Uri uri) {
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        // Upload to Cloud Storage
        String uuid = UUID.randomUUID().toString();
        mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(uri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        if (BuildConfig.DEBUG) {
//                            Log.d(TAG, "uploadPhoto:onSuccess:" +
//                                    taskSnapshot.getMetadata().getReference().getPath());
//                        }
                        Toast.makeText(ProfileActivity.this, "Image uploaded",
                                Toast.LENGTH_SHORT).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                imageUri = task.getResult().toString();

                                Map<String, Object> profile = new HashMap<>();
                                profile.put("photo", imageUri);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {

                                    db.collection("coach").document(user.getUid())
                                            .set(profile, SetOptions.merge());
                                }

                                GlideApp.with(ProfileActivity.this)
                                        .load(imageUri)
                                        .centerCrop()
                                        .into(cek);
                            }
                        });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "uploadPhoto:onError", e);
                        Toast.makeText(ProfileActivity.this, "Upload failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setProfile() {

        String topic = mBinding.topic.getText().toString();
        String description = mBinding.description.getText().toString();

        if (TextUtils.isEmpty(topic))
        {
            Toast.makeText(ProfileActivity.this, "Please enter topic...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(description))
        {
            Toast.makeText(ProfileActivity.this, "Please enter description...", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Map<String, Object> userNew = new HashMap<>();
                userNew.put("topic", topic);
                userNew.put("description", description);

                db.collection("coach").document(user.getUid())
                        .set(userNew, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // See #choosePhoto with @AfterPermissionGranted
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,
                Collections.singletonList(PERMS))) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileImage:
                choosePhoto();
                break;
            case R.id.finish:
                setProfile();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.detail:
                Intent detailIntent = new Intent(ProfileActivity.this, DetailCoachActivity.class);
                detailIntent.putExtra(DetailCoachActivity.KEY_COACH_ID, user.getUid());

                startActivity(detailIntent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
        }
    }
}