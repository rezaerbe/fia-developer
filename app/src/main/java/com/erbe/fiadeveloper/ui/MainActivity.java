package com.erbe.fiadeveloper.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityMainBinding;
import com.erbe.fiadeveloper.ui.article.CategoryArticleActivity;
import com.erbe.fiadeveloper.ui.coaching.ListCoachActivity;
import com.erbe.fiadeveloper.ui.coaching.ListCoachingActivity;
import com.erbe.fiadeveloper.ui.consultation.ListConsultantActivity;
import com.erbe.fiadeveloper.ui.consultation.ListConsultationActivity;
import com.erbe.fiadeveloper.ui.original.CategoryOriginalActivity;
import com.erbe.fiadeveloper.ui.report.ReportActivity;
import com.erbe.fiadeveloper.ui.video.ListVideoActivity;
import com.erbe.fiadeveloper.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Firestore
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mBinding.user.setText(user.getDisplayName());
        }

        mBinding.report.setOnClickListener(this);
        mBinding.coach.setOnClickListener(this);
        mBinding.consultant.setOnClickListener(this);
        mBinding.coaching.setOnClickListener(this);
        mBinding.consultation.setOnClickListener(this);
        mBinding.article.setOnClickListener(this);
        mBinding.video.setOnClickListener(this);
        mBinding.original.setOnClickListener(this);
//        mBinding.listReport.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.menu_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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

            FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
            if (users != null) {
                Map<String, Object> userNew = new HashMap<>();
                userNew.put("userId", users.getUid());
                userNew.put("userName", users.getDisplayName());

                // Todo: Coach and Consultant Change
                db.collection("user").document(users.getUid())
                        .set(userNew, SetOptions.merge());
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report:
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
                break;
            case R.id.article:
                startActivity(new Intent(MainActivity.this, CategoryArticleActivity.class));
                break;
            case R.id.video:
                startActivity(new Intent(MainActivity.this, ListVideoActivity.class));
                break;
            case R.id.original:
                startActivity(new Intent(MainActivity.this, CategoryOriginalActivity.class));
                break;
            case R.id.coach:
                startActivity(new Intent(MainActivity.this, ListCoachActivity.class));
                break;
            case R.id.consultant:
                startActivity(new Intent(MainActivity.this, ListConsultantActivity.class));
                break;
            case R.id.coaching:
                startActivity(new Intent(MainActivity.this, ListCoachingActivity.class));
                break;
            case R.id.consultation:
                startActivity(new Intent(MainActivity.this, ListConsultationActivity.class));
                break;
//            case R.id.listReport:
//                startActivity(new Intent(MainActivity.this, ListReportActivity.class));
//                break;
        }
    }
}