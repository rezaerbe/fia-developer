package com.fia.femaleinaction.ui.content;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.databinding.ActivityOriginalContentBinding;
import com.fia.femaleinaction.model.Original;
import com.fia.femaleinaction.util.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class OriginalContentActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private ActivityOriginalContentBinding mBinding;

    public static final String ORIGINAL_CATEGORY_ID = "original_category_id";

    private static final String TAG = "Image";
    private static final int RC_CHOOSE_PHOTO = 101;
    private static final int RC_IMAGE_PERMS = 102;
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;

    private StorageReference mImageRef;

    private FirebaseFirestore db;

    private String imageUri;

    private Uri selectedImage;

    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOriginalContentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Get category ID from extras
        categoryId = getIntent().getExtras().getString(ORIGINAL_CATEGORY_ID);
        if (categoryId == null) {
            throw new IllegalArgumentException("Must pass extra " + ORIGINAL_CATEGORY_ID);
        }

        mBinding.originalImage.setOnClickListener(this);
        mBinding.finish.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                selectedImage = data.getData();
                GlideApp.with(OriginalContentActivity.this)
                        .load(selectedImage)
                        .centerCrop()
                        .placeholder(R.drawable.empty)
                        .into(mBinding.originalImage);
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

    private void setOriginalContent() {

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        String title = mBinding.title.getText().toString();
        String source = mBinding.source.getText().toString();
        String description = mBinding.description.getText().toString();

        if (TextUtils.isEmpty(title))
        {
            Toast.makeText(OriginalContentActivity.this, "Please enter title...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(source))
        {
            Toast.makeText(OriginalContentActivity.this, "Please enter source...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(description))
        {
            Toast.makeText(OriginalContentActivity.this, "Please enter description...", Toast.LENGTH_SHORT).show();
        }
        if (selectedImage == null)
        {
            Toast.makeText(OriginalContentActivity.this, "Please enter image...", Toast.LENGTH_SHORT).show();
        }
        else {

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            String uuid = UUID.randomUUID().toString();

            mImageRef = FirebaseStorage.getInstance().getReference("original").child(uuid);
            mImageRef.putBytes(data)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(OriginalContentActivity.this, "Image uploaded",
                                    Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    imageUri = Objects.requireNonNull(task.getResult()).toString();

                                    Original original = new Original(title, source, description, imageUri);

                                    db.collection("original").document(categoryId).collection("listoriginal")
                                            .add(original)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    mBinding.progressLoading.setVisibility(View.GONE);
                                                    Toast.makeText(OriginalContentActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(OriginalContentActivity.this, MainActivity.class));
                                                    onBackPressed();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                    mBinding.progressLoading.setVisibility(View.GONE);
                                                    onBackPressed();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "uploadPhoto:onError", e);
                            Toast.makeText(OriginalContentActivity.this, "Upload failed",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
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
            case R.id.originalImage:
                choosePhoto();
                break;
            case R.id.finish:
                setOriginalContent();
                break;
        }
    }
}
