package com.erbe.fiadeveloper.ui.coaching;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.ChatHolder;
import com.erbe.fiadeveloper.databinding.ActivityCoachingBinding;
import com.erbe.fiadeveloper.model.Chat;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * Class demonstrating how to setup a {@link RecyclerView} with an adapter while taking sign-in
 * states into consideration. Also demonstrates adding data to a ref and then reading it back using
 * the {@link FirestoreRecyclerAdapter} to build a simple chat app.
 * <p>
 * For a general intro to the RecyclerView, see <a href="https://developer.android.com/training/material/lists-cards.html">Creating
 * Lists</a>.
 */
@SuppressLint("RestrictedApi")
public class CoachingActivity extends AppCompatActivity
        implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "FirestoreChatActivity";

    private ActivityCoachingBinding mBinding;

    private CollectionReference sChatCollection;
    /** Get the last 50 chat messages ordered by timestamp . */
    private Query sChatQuery;

    public static final String KEY_COACHING_ID = "key_coaching_id";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCoachingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get category ID from extras
        String coachingId = getIntent().getExtras().getString(KEY_COACHING_ID);
        if (coachingId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_COACHING_ID);
        }

        sChatCollection = FirebaseFirestore.getInstance().collection("coaching").document(coachingId).collection("chats");

        sChatQuery = sChatCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        mBinding.messagesList.setHasFixedSize(true);
        mBinding.messagesList.setLayoutManager(manager);

        mBinding.messagesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mBinding.messagesList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.messagesList.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });

//        ImeHelper.setImeOnDoneListener(mBinding.messageEdit, new ImeHelper.DonePressedListener() {
//            @Override
//            public void onDonePressed() {
//                onSendClick();
//            }
//        });

        mBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        mBinding.sendButton.setEnabled(isSignedIn());
        mBinding.messageEdit.setEnabled(isSignedIn());

        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(this, "Error: maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mBinding.messagesList.smoothScrollToPosition(0);
            }
        });

        mBinding.messagesList.setAdapter(adapter);
    }

    public void onSendClick() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message = mBinding.messageEdit.getText().toString();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please insert your message", Toast.LENGTH_SHORT).show();
        } else if (message.replaceAll("\\s+", "").equals("")) {
            Toast.makeText(this, "Please insert your message", Toast.LENGTH_SHORT).show();
        } else {
            onAddMessage(new Chat(name, mBinding.messageEdit.getText().toString(), uid));
        }

        mBinding.messageEdit.setText("");
    }

    @NonNull
    private RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(sChatQuery, Chat.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirestoreRecyclerAdapter<Chat, ChatHolder>(options) {
            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                mBinding.emptyTextView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void onAddMessage(@NonNull Chat chat) {
        sChatCollection.add(chat).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to write message", e);
            }
        });
    }
}
