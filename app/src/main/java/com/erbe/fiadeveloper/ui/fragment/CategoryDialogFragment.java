package com.erbe.fiadeveloper.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.DialogCategoryBinding;
import com.erbe.fiadeveloper.model.Category;

/**
 * Dialog Fragment containing category form.
 */
public class CategoryDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "CategoryDialog";

    private DialogCategoryBinding mBinding;

    public interface CategoryListener {

        void onCategory(Category category);

    }

    private CategoryListener mCategoryListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DialogCategoryBinding.inflate(inflater, container, false);

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

        if (context instanceof CategoryListener) {
            mCategoryListener = (CategoryListener) context;
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
        Category category = new Category(mBinding.userFormText.getText().toString());

        if (mCategoryListener != null && !TextUtils.isEmpty(mBinding.userFormText.getText().toString())) {
            mCategoryListener.onCategory(category);
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
