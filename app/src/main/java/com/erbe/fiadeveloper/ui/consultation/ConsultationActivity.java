package com.erbe.fiadeveloper.ui.consultation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.erbe.fiadeveloper.R;

public class ConsultationActivity extends AppCompatActivity {

    public static final String KEY_CONSULTATION_ID = "key_consultation_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
    }
}