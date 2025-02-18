package com.fia.femaleinaction.ui.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.adapter.VideoAdapter;
import com.fia.femaleinaction.databinding.ActivityListVideoBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ListVideoActivity extends AppCompatActivity implements VideoAdapter.OnVideoSelectedListener {

    private static final String TAG = "ListVideoActivity";

    private ActivityListVideoBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private VideoAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListVideoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Todo: Writer Uncomment
//        mBinding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ListVideoActivity.this, VideoContentActivity.class);
//                startActivity(intent);
//            }
//        });

        // Get video
        mQuery = mFirestore
                .collection("video")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // RecyclerView
        mAdapter = new VideoAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerVideo.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerVideo.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: maaf terjadi kesalahan.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.recyclerVideo.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerVideo.setAdapter(mAdapter);
        mBinding.progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onVideoSelected(DocumentSnapshot video) {
        // Go to the details page for the selected video
        Intent intent = new Intent(this, DetailVideoActivity.class);
        intent.putExtra(DetailVideoActivity.KEY_VIDEO_ID, video.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}