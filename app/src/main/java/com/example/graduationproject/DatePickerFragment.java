package com.example.graduationproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnFinish onFinish;
    private List<Integer> dateTime = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Do something with the date chosen by the user
        dateTime.add(0, day);
        dateTime.add(1, month);
        dateTime.add(2, year);
        onFinish.finish(dateTime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onFinish = (OnFinish) context;
    }

    public interface OnFinish {
        void finish(List<Integer> dateTime);
    }
}
