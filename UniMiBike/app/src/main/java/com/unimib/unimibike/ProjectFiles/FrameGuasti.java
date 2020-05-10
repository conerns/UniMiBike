package com.unimib.unimibike.ProjectFiles;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.unimibike.R;

public class FrameGuasti extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String[] COUNTRIES = new String[] {"Item 1", "Item 2", "Item 3", "Item 4"};

        View w = inflater.inflate(R.layout.fragment_guasti,container, false);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        COUNTRIES);

        AutoCompleteTextView editTextFilledExposedDropdown =
                w.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setInputType(InputType.TYPE_NULL);
        return w;

    }
}
