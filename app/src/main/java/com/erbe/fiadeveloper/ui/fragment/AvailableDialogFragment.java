package com.erbe.fiadeveloper.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.DialogAvailableBinding;
import com.erbe.fiadeveloper.model.Available;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
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

    private String dateFrom, dateTo;

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

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm MM/dd/yyyy", Locale.US);
        Date from;
        Date to;

        try {
            from = formatter.parse(dateFrom);
            to = formatter.parse(dateTo);
            Available available = new Available(from, to);

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
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        TimePickerDialog from = new TimePickerDialog(getContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hour1, int minute1) {

                                        TimePickerDialog to = new TimePickerDialog(getContext(),
                                                new TimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hour2, int minute2) {

                                                        dateFrom = hour1 + ":" + minute1 + " " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                                        dateTo = hour2 + ":" + minute2 + " " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                                        String dateFinal = hour1 + ":" + minute1 + " - " + hour2 + ":" + minute2 + " " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                                                        mBinding.userFormText.setText(dateFinal);

                                                    }
                                                }, hour, minute, true);
                                        to.show();

                                    }
                                }, hour, minute, true);
                        from.show();

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
