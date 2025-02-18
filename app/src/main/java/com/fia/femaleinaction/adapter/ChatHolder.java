package com.fia.femaleinaction.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatHolder extends RecyclerView.ViewHolder {

//    private final TextView mNameField;
    private final TextView mTextField;
//    private final TextView mDateField;
    //    private final FrameLayout mLeftArrow;
//    private final FrameLayout mRightArrow;
    private final RelativeLayout mMessageContainer;
    private final LinearLayout mMessage;
    private final int mPurple200;
    private final int mTeal200;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);
//        mNameField = itemView.findViewById(R.id.name_text);
        mTextField = itemView.findViewById(R.id.message_text);
//        mDateField = itemView.findViewById(R.id.date_text);
//        mLeftArrow = itemView.findViewById(R.id.left_arrow);
//        mRightArrow = itemView.findViewById(R.id.right_arrow);
        mMessageContainer = itemView.findViewById(R.id.message_container);
        mMessage = itemView.findViewById(R.id.message);
        mPurple200 = ContextCompat.getColor(itemView.getContext(), R.color.purple_200);
        mTeal200 = ContextCompat.getColor(itemView.getContext(), R.color.teal_200);
    }

    public void bind(@NonNull Chat chat) {
//        setName(chat.getName());
        setMessage(chat.getMessage());
//        setDate(chat.getTimestamp());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        setIsSender(currentUser != null && chat.getUid().equals(currentUser.getUid()));
    }

//    private void setName(@Nullable String name) {
//        mNameField.setText(name);
//    }

//    private void setDate(Date date) {
//
//        final SimpleDateFormat FORMAT  = new SimpleDateFormat(
//                "HH:mm  |  dd/MM/yyyy", Locale.US);
//
//        mDateField.setText(FORMAT.format(date));
//    }

    private void setMessage(@Nullable String text) {
        mTextField.setText(text);
    }

    private void setIsSender(boolean isSender) {
        final int color;
        if (isSender) {
            color = mPurple200;
//            mLeftArrow.setVisibility(View.GONE);
//            mRightArrow.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) mMessage.getLayoutParams();
            marginParams.setMargins(84,8,0,8);
            mMessageContainer.setGravity(Gravity.END);
        } else {
            color = mTeal200;
//            mLeftArrow.setVisibility(View.VISIBLE);
//            mRightArrow.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) mMessage.getLayoutParams();
            marginParams.setMargins(0,8,84,8);
            mMessageContainer.setGravity(Gravity.START);
        }

        ((GradientDrawable) mMessage.getBackground()).setColor(color);
//        ((RotateDrawable) mLeftArrow.getBackground()).getDrawable()
//                .setColorFilter(color, PorterDuff.Mode.SRC);
//        ((RotateDrawable) mRightArrow.getBackground()).getDrawable()
//                .setColorFilter(color, PorterDuff.Mode.SRC);
    }
}
