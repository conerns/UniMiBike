package com.unimib.unimibike.ProjectFiles;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

public class FrameGuasti extends Fragment {
    private String get_email;
    private String get_role;
    private int get_id;
    private View mdivider;
    private TextInputLayout mAdminIdBike;
    private TextInputLayout mIdBike;
    private TextInputLayout mType;
    private TextInputLayout mDescription;
    private Button mSendReportAdmin;
    private Button mSendReport;
    View w;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String[] COUNTRIES = new String[] {getString(R.string.flat_tire), getString(R.string.broken_chain),
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
        w = inflater.inflate(R.layout.fragment_guasti,container, false);
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(get_role.equals("admin")){
            mAdminIdBike = view.findViewById(R.id.bike_code_fixed);
            mAdminIdBike.setVisibility(View.VISIBLE);
            mdivider = view.findViewById(R.id.divider2);
            mdivider.setVisibility(View.VISIBLE);
            mSendReportAdmin = view.findViewById(R.id.send_fixed_report);
            mSendReportAdmin.setVisibility(View.VISIBLE);
        }
        mIdBike = view.findViewById(R.id.bike_code_fault);
        mType = view.findViewById(R.id.type_fault_report);
        mDescription = view.findViewById(R.id.bike_falut_desciption);

        mSendReport = view.findViewById(R.id.send_fault_report);
        mSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkType(mType.getEditText().getText().toString()) & checkIdBike(mIdBike.getEditText().getText().toString())) {
                    Report r = createReport();
                    newReport(r);
                }
            }
        });
    }

    public boolean checkType(String value){
        if(value.isEmpty()){
            mType.setError("Campo obbligatorio");
            return false;
        }
        mType.setError(null);
        mType.setErrorEnabled(false);
        return true;
    }

    public boolean checkIdBike(String value){
        if(value.isEmpty()){
            mIdBike.setError("Campo obbligatorio");
            return false;
        }
        mIdBike.setError(null);
        mIdBike.setErrorEnabled(false);
        return true;
    }

    public Report createReport(){
        String typeTemp = mType.getEditText().getText().toString();
        Report report = new Report();
        report.setBikeId(Integer.parseInt(mIdBike.getEditText().getText().toString()));
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
        report.setDescription(mDescription.getEditText().getText().toString());
        report.setUserId(get_id);
        return report;
    }


    public void newReport(Report report){
        UnimibBikeFetcher.postReport(getActivity().getApplicationContext(),report, new ServerResponseParserCallback<Report>() {
            @Override
            public void onSuccess(Report response) {
                DialogFragment newFragment = MyAlertDialogFragment.newInstance(getString(R.string.report_sent), getString(R.string.report_sent_body));
                newFragment.show(getFragmentManager(), "dialog");
                mIdBike.getEditText().setText("");
                mType.getEditText().setText("");
                mDescription.getEditText().setText("");
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {

            }
        });
    }

}


