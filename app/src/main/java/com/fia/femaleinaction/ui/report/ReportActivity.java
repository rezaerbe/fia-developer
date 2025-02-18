package com.fia.femaleinaction.ui.report;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fia.femaleinaction.databinding.ActivityReportBinding;
import com.fia.femaleinaction.model.Report;
import com.fia.femaleinaction.ui.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportActivity extends AppCompatActivity {

    ActivityReportBinding mBinding;

    private FirebaseFirestore db;

    private static final String TAG = "Report";

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Firestore
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        mBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.progressLoading.setVisibility(View.VISIBLE);

                uploadReport();
            }
        });
    }

    private void uploadReport() {

        String status = mBinding.status.getText().toString();
        String type = mBinding.type.getText().toString();
        String phone = mBinding.phone.getText().toString();
        String chronology = mBinding.chronology.getText().toString();

        if (TextUtils.isEmpty(status))
        {
            Toast.makeText(ReportActivity.this, "Please enter status...", Toast.LENGTH_SHORT).show();
            mBinding.progressLoading.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(type))
        {
            Toast.makeText(ReportActivity.this, "Please enter type...", Toast.LENGTH_SHORT).show();
            mBinding.progressLoading.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(ReportActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
            mBinding.progressLoading.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(chronology))
        {
            Toast.makeText(ReportActivity.this, "Please enter chronology...", Toast.LENGTH_SHORT).show();
            mBinding.progressLoading.setVisibility(View.GONE);
        }
        else {
            if (user != null) {

                Report reportNew = new Report(user.getUid(), user.getDisplayName(), status, type, phone, chronology);

                db.collection("report")
                        .add(reportNew)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                mBinding.progressLoading.setVisibility(View.GONE);
                                Toast.makeText(ReportActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ReportActivity.this, MainActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                mBinding.progressLoading.setVisibility(View.GONE);
                            }
                        });
            }

        }
    }
}