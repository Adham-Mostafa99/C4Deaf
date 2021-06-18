package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.models.SpinnerModel;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {

    public SpinnerAdapter(@NonNull Context context, ArrayList<SpinnerModel> spinnerModels) {
        super(context, 0, spinnerModels);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    public View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_row, parent,false);
        }

        ImageView imageView = convertView.findViewById(R.id.spinner_icon);
        TextView textView = convertView.findViewById(R.id.spinner_text);

        SpinnerModel current = getItem(position);
        if (current != null) {
            imageView.setImageResource(current.getIcon());
            textView.setText(current.getText());
        }
        return convertView;
    }
}
