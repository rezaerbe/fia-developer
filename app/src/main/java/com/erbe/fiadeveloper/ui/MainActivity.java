package com.erbe.fiadeveloper.ui;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityMainBinding;
import com.erbe.fiadeveloper.ui.article.CategoryArticleActivity;
import com.erbe.fiadeveloper.ui.coaching.ListCoachActivity;
import com.erbe.fiadeveloper.ui.coaching.ListCoachingActivity;
import com.erbe.fiadeveloper.ui.consultation.ListConsultantActivity;
import com.erbe.fiadeveloper.ui.consultation.ListConsultationActivity;
import com.erbe.fiadeveloper.ui.original.CategoryOriginalActivity;
import com.erbe.fiadeveloper.ui.video.ListVideoActivity;
import com.erbe.fiadeveloper.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private ActivityMainBinding mBinding;

    private MainActivityViewModel mViewModel;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Firestore
        db = FirebaseFirestore.getInstance();

        mBinding.button1.setOnClickListener(this);
        mBinding.button2.setOnClickListener(this);
        mBinding.button3.setOnClickListener(this);
        mBinding.button4.setOnClickListener(this);
        mBinding.button5.setOnClickListener(this);
        mBinding.button6.setOnClickListener(this);
        mBinding.button7.setOnClickListener(this);
        mBinding.button8.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            mViewModel.setIsSigningIn(false);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Map<String, Object> userNew = new HashMap<>();
                userNew.put("userId", user.getUid());
                userNew.put("userName", user.getDisplayName());

                db.collection("user").document(user.getUid())
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

            startActivity(new Intent(MainActivity.this, ProfileActivity.class));

            if (resultCode != RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish();
                } else if (response.getError() != null
                        && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.message_no_network);
                } else {
                    showSignInErrorDialog(R.string.message_unknown);
                }
            }
        }
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_sign_in_error)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.option_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                })
                .setNegativeButton(R.string.option_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(MainActivity.this, CategoryArticleActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(MainActivity.this, ListVideoActivity.class));
                break;
            case R.id.button4:
                startActivity(new Intent(MainActivity.this, CategoryOriginalActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(MainActivity.this, ListCoachActivity.class));
                break;
            case R.id.button6:
                startActivity(new Intent(MainActivity.this, ListConsultantActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(MainActivity.this, ListCoachingActivity.class));
                break;
            case R.id.button8:
                startActivity(new Intent(MainActivity.this, ListConsultationActivity.class));
                break;
        }
    }
}