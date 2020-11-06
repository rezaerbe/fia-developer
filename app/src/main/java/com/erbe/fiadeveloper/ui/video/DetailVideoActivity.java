package com.erbe.fiadeveloper.ui.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailVideoBinding;
import com.erbe.fiadeveloper.model.Video;
import com.erbe.fiadeveloper.ui.ProfileActivity;
import com.erbe.fiadeveloper.util.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DetailVideoActivity extends AppCompatActivity {

    private static final String TAG = "DetailVideo";

    public static final String KEY_VIDEO_ID = "key_video_id";

    private ActivityDetailVideoBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mVideoRef;
    private ListenerRegistration mVideoRegistration;

    String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailVideoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get restaurant ID from extras
        String videoId = getIntent().getExtras().getString(KEY_VIDEO_ID);
        if (videoId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_VIDEO_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mVideoRef = mFirestore.collection("video").document(videoId);

        mVideoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getString("link") != null) {
                        WebView myWebView = new WebView(DetailVideoActivity.this);
                        setContentView(myWebView);
                        WebSettings webSettings = myWebView.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        myWebView.loadUrl(document.getString("link"));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mVideoRegistration = mVideoRef.addSnapshotListener(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        if (mVideoRegistration != null) {
//            mVideoRegistration.remove();
//            mVideoRegistration = null;
//        }
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

//    /**
//     * Listener for the Restaurant document ({@link #mVideoRef}).
//     */
//    @Override
//    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//        if (e != null) {
//            Log.w(TAG, "video:onEvent", e);
//            return;
//        }
//
//        onVideoLoaded(snapshot.toObject(Video.class));
//    }
//
//    private void onVideoLoaded(Video video) {
//
//        WebView webView = findViewById(R.id.videoWeb);
//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
//
//        // Tiga baris di bawah ini agar laman yang dimuat dapat
//        // melakukan zoom.
//        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setDisplayZoomControls(false);
//        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
//        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl(video.getLink());
//    }
}