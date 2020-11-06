package com.erbe.fiadeveloper.ui.article;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailArticleBinding;
import com.erbe.fiadeveloper.model.Article;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DetailArticleActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailArticle";

    public static final String KEY_ARTICLE_ID = "key_article_id";
    public static final String ARTICLE_CATEGORY_ID = "article_category_id";

    private ActivityDetailArticleBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mArticleRef, mListArticleRef;
    private ListenerRegistration mArticleRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailArticleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get category ID from extras
        String categoryId = getIntent().getExtras().getString(ARTICLE_CATEGORY_ID);
        if (categoryId == null) {
            throw new IllegalArgumentException("Must pass extra " + ARTICLE_CATEGORY_ID);
        }

        // Get article ID from extras
        String articleId = getIntent().getExtras().getString(KEY_ARTICLE_ID);
        if (articleId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_ARTICLE_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the article
        mArticleRef = mFirestore.collection("article").document(categoryId);
        mListArticleRef = mArticleRef.collection("listarticle").document(articleId);

    }

    @Override
    public void onStart() {
        super.onStart();

        mArticleRegistration = mListArticleRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mArticleRegistration != null) {
            mArticleRegistration.remove();
            mArticleRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Article document ({@link #mArticleRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "article:onEvent", e);
            return;
        }

        onArticleLoaded(snapshot.toObject(Article.class));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void onArticleLoaded(Article article) {

        WebView webView = findViewById(R.id.articleWeb);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mBinding.progressLoading.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(article.getLink());
    }

}