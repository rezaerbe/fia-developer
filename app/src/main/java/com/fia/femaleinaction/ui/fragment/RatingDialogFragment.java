package com.fia.femaleinaction.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.databinding.DialogRatingBinding;
import com.fia.femaleinaction.model.Rating;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Dialog Fragment containing rating form.
 */
public class RatingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RatingDialog";

    private DialogRatingBinding mBinding;

    public interface RatingListener {

        void onRating(Rating rating);

    }

    private RatingListener mRatingListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DialogRatingBinding.inflate(inflater, container, false);

        mBinding.userFormButton.setOnClickListener(this);
        mBinding.userFormCancel.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            mRatingListener = (RatingListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void onSubmitClicked(View view) {
        Rating rating = new Rating(
                FirebaseAuth.getInstance().getCurrentUser(),
                mBinding.userFormRating.getRating(),
                mBinding.userFormText.getText().toString());

        if (mRatingListener != null && mBinding.userFormRating.getRating() != 0 && !TextUtils.isEmpty(mBinding.userFormText.getText().toString())) {
            mRatingListener.onRating(rating);
        } else {
            Toast.makeText(getContext(), "Please enter your review...", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    private void onCancelClicked(View view) {
        dismiss();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userFormButton:
                onSubmitClicked(v);
                break;
            case R.id.userFormCancel:
                onCancelClicked(v);
                break;
        }
    }

}

