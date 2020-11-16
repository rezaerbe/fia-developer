package com.erbe.fiadeveloper.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.DialogAvailableBinding;
import com.erbe.fiadeveloper.model.Available;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Dialog Fragment containing available form.
 */
public class AvailableDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "AvailableDialog";

    private DialogAvailableBinding mBinding;

    public interface AvailableListener {

        void onAvailable(Available available);

    }

    private AvailableListener mAvailableListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DialogAvailableBinding.inflate(inflater, container, false);

        mBinding.userFormButton.setOnClickListener(this);
        mBinding.userFormCancel.setOnClickListener(this);
        mBinding.userFormText.setOnClickListener(this);

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

        if (context instanceof AvailableListener) {
            mAvailableListener = (AvailableListener) context;
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

        String date = mBinding.userFormText.getText().toString();
        SimpleDateFormat firstFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date d;

        try {
            d = firstFormatter.parse(date);
            Available available = new Available(d);

            if (mAvailableListener != null) {
                mAvailableListener.onAvailable(available);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        dismiss();
    }

    private void setDate() {
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mBinding.userFormText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    private void onCancelClicked(View view) {
        dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            case R.id.userFormText:
                setDate();
                break;
        }
    }

}
