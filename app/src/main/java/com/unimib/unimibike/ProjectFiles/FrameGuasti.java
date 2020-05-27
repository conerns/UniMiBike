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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.ProjectFiles.ViewModels.ReportsViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentGuastiBinding;

public class FrameGuasti extends Fragment {
    private String get_email;
    private String get_role;
    private int get_id;
    private ReportsViewModel reportsViewModel;
    private MutableLiveData<Report> reportMutableLiveDate;
    private FragmentGuastiBinding binding;
    View w;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String[] TYPEOFREPORT = new String[] {getString(R.string.flat_tire), getString(R.string.broken_chain),
                                            getString(R.string.broken_pedal), getString(R.string.saddle_absent),
                                            getString(R.string.absent_tire), getString(R.string.malfunctioning_breaks), getString(R.string.altro_guasto)
                            };
        if(SaveSharedPreference.getUserName(getContext().getApplicationContext()).length() != 0) {
            get_email = SaveSharedPreference.getUserName(getContext().getApplicationContext());
            get_role = SaveSharedPreference.getPrefUserRole(getContext().getApplicationContext());
            get_id = SaveSharedPreference.getUserID(getContext().getApplicationContext());
        }else {
            get_role = getArguments().getString("USER-ROLE");
            get_email = getArguments().getString("USER-MAIL");
            get_id = getArguments().getInt("USER-ID");
        }

        binding = FragmentGuastiBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        TYPEOFREPORT);

        AutoCompleteTextView editTextFilledExposedDropdown =
                view.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setInputType(InputType.TYPE_NULL);
        return view;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(get_role.equals("admin")){
            binding.divider2.setVisibility(View.VISIBLE);
            binding.bikeCodeFixed.setVisibility(View.VISIBLE);
            binding.sendFixedReport.setVisibility(View.VISIBLE);
        }

        binding.sendFaultReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkType(binding.typeFaultReport.getEditText().getText().toString()) & checkIdBike(binding.bikeCodeFault.getEditText().getText().toString())) {
                    Report r = createReport();
                    newReport(r);
                }
            }
        });
    }

    public boolean checkType(String value){
        if(value.isEmpty()){
            binding.typeFaultReport.setError("Campo obbligatorio");
            return false;
        }
        binding.typeFaultReport.setError(null);
        binding.typeFaultReport.setErrorEnabled(false);
        return true;
    }

    public boolean checkIdBike(String value){
        if(value.isEmpty()){
            binding.bikeCodeFault.setError("Campo obbligatorio");
            return false;
        }
        binding.bikeCodeFault.setError(null);
        binding.bikeCodeFault.setErrorEnabled(false);
        return true;
    }

    public Report createReport(){
        String typeTemp = binding.typeFaultReport.getEditText().getText().toString();
        Report report = new Report();
        report.setBikeId(Integer.parseInt(binding.bikeCodeFault.getEditText().getText().toString()));
        if(typeTemp.equals(getString(R.string.flat_tire)))
            report.setType(1);
        else if(typeTemp.equals(getString(R.string.broken_chain)))
            report.setType(2);
        else if(typeTemp.equals(getString(R.string.broken_pedal)))
            report.setType(3);
        else if(typeTemp.equals(getString(R.string.absent_tire)))
            report.setType(4);
        else if(typeTemp.equals(getString(R.string.saddle_absent)))
            report.setType(5);
        else if(typeTemp.equals(getString(R.string.malfunctioning_breaks)))
            report.setType(6);
        report.setDescription(binding.bikeFalutDesciption.getEditText().getText().toString());
        report.setUserId(get_id);
        return report;
    }


    public void newReport(Report report){
        reportsViewModel = new ReportsViewModel();
        final Observer<Report> observer = new Observer<Report>() {
            @Override
            public void onChanged(Report report) {
                DialogFragment newFragment = MyAlertDialogFragment.newInstance(getString(R.string.report_sent), getString(R.string.report_sent_body));
                newFragment.show(getFragmentManager(), "dialog");
                binding.bikeCodeFault.getEditText().setText("");
                binding.typeFaultReport.getEditText().setText("");
                binding.bikeFalutDesciption.getEditText().setText("");
            }
        };
        reportMutableLiveDate = reportsViewModel.sendReport(getContext(), report);

        reportMutableLiveDate.observe(requireActivity(), observer);
    }

}


