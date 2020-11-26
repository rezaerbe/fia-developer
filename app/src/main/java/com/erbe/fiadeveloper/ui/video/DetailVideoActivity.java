package com.erbe.fiadeveloper.ui.video;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailVideoBinding;
import com.erbe.fiadeveloper.model.Video;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DetailVideoActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailVideo";

    public static final String KEY_VIDEO_ID = "key_video_id";

    private ActivityDetailVideoBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mVideoRef;
    private ListenerRegistration mVideoRegistration;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailVideoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        webView = findViewById(R.id.videoWeb);

        // Get video ID from extras
        String videoId = getIntent().getExtras().getString(KEY_VIDEO_ID);
        if (videoId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_VIDEO_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the video
        mVideoRef = mFirestore.collection("video").document(videoId);

    }

    @Override
    public void onStart() {
        super.onStart();

        mVideoRegistration = mVideoRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mVideoRegistration != null) {
            mVideoRegistration.remove();
            mVideoRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Video document ({@link #mVideoRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "video:onEvent", e);
            return;
        }

        onVideoLoaded(snapshot.toObject(Video.class));
    }

    private void onVideoLoaded(Video video) {

        mBinding.videoTitleDetail.setText(video.getTitle());
        mBinding.videoSourceDetail.setText(video.getSource());

        // Background image
        Glide.with(mBinding.videoImageDetail.getContext())
                .load(video.getImage())
                .centerCrop()
                .placeholder(R.drawable.empty)
                .into(mBinding.videoImageDetail);

        mBinding.progressLoading.setVisibility(View.GONE);

        mBinding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(video.getLink());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}