package com.unimib.unimibike.ProjectFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.unimibike.Model.Bike;
import com.unimib.unimibike.Model.Report;
import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.ProjectFiles.AdminOperations.AggiungiNuovaBici;
import com.unimib.unimibike.ProjectFiles.ViewModels.ReportsViewModel;
import com.unimib.unimibike.R;
import com.unimib.unimibike.Util.Costants;
import com.unimib.unimibike.Util.MyAlertDialogFragment;
import com.unimib.unimibike.Util.MyUtils;
import com.unimib.unimibike.Util.QrReaderActivity;
import com.unimib.unimibike.Util.SaveSharedPreference;
import com.unimib.unimibike.databinding.FragmentGuastiBinding;

public class FrameGuasti extends Fragment {
    private String get_email;
    private String get_role;
    private int get_id;
    private ReportsViewModel reportsViewModel;
    private MutableLiveData <Resource<Report>> reportMutableLiveDate;
    private FragmentGuastiBinding binding;
    View w;
    @SuppressLint("ClickableViewAccessibility")
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
            get_role = getArguments().getString(Costants.USER_ROLE);
            get_email = getArguments().getString(Costants.USER_MAIL);
            get_id = getArguments().getInt(Costants.USER_ID);
        }
        binding = FragmentGuastiBinding.inflate(getLayoutInflater());
        binding.bikeFalutDesciptionText.setFilters((new InputFilter[]{new InputFilter.LengthFilter(120)}));
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

        binding.bikeCodeTextFixed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.bikeCodeTextFixed.getRight() - binding.bikeCodeTextFixed.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission(1);
                        return true;
                    }
                }
                return false;
            }
        });
        binding.bikeCodeTextFault.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.bikeCodeTextFault.getRight() - binding.bikeCodeTextFault.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission(0);
                        return true;
                    }
                }
                return false;
            }
        });
        binding.valoriRastrelliereFine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (binding.valoriRastrelliereFine.getRight() - binding.valoriRastrelliereFine.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCameraPermission(2);
                        return true;
                    }
                }
                return false;
            }
        });
        return view;

    }

    private void sendFixReport() {
        MutableLiveData<Resource<Bike>> bike;
        reportsViewModel = new ReportsViewModel();
        Observer<Resource<Bike>> observer = new Observer<Resource<Bike>>() {
            @Override
            public void onChanged(Resource<Bike> bikeResource) {


                Log.d("TAGunico", bikeResource.getStatusCode()+"");
                if(bikeResource.getStatusCode() == 200) {
                    if(bikeResource.getData() != null) {
                        binding.valoriRastrelliereFine.setText(null);
                        binding.bikeCodeTextFixed.setText(null);
                        funzione_dialog();
                    }
                    else{
                        bikeNotFixableDialog();
                    }
                }else if(bikeResource.getStatusCode() == 404){
                    binding.bikeCodeFixed.setErrorEnabled(true);
                    binding.bikeCodeFixed.setError(getString(R.string.insert_vaild_value));
                    binding.posizioneBiciNuova.setErrorEnabled(true);
                    binding.posizioneBiciNuova.setError(getString(R.string.insert_vaild_value));
                    binding.bikeCodeFixed.clearFocus();
                    binding.posizioneBiciNuova.clearFocus();
                }
            }
        };
        bike = reportsViewModel.fixReport(getActivity().getApplicationContext(),
                SaveSharedPreference.getUserID(getActivity().getApplicationContext()),
                Integer.parseInt(binding.valoriRastrelliereFine.getText().toString()),
                Integer.parseInt(binding.bikeCodeTextFixed.getText().toString())
        );
        bike.observe(this, observer);
    }

    private void funzione_dialog() {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(getString(R.string.fixed_message), getString(R.string.fixed_message_text));
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void bikeNotFixableDialog(){
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(getString(R.string.bike_not_fixable_title), getString(R.string.bike_not_fixable_message));
        newFragment.show(getFragmentManager(), "dialog");
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(get_role.equals("admin")){
            binding.divider2.setVisibility(View.VISIBLE);
            binding.bikeCodeFixed.setVisibility(View.VISIBLE);
            binding.sendFixedReport.setVisibility(View.VISIBLE);
            binding.posizioneBiciNuova.setVisibility(View.VISIBLE);
        }

        binding.sendFaultReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sendFaultReport", binding.typeFaultReport.getEditText().getText().toString());
                Log.d("sendFaultReport", binding.bikeCodeFault.getEditText().getText().toString());
                //binding.typeFaultReport.clearFocus();
                //binding.bikeCodeFault.clearFocus();
                if(checkType(binding.typeFaultReport.getEditText().getText().toString())
                    & checkIdBike(binding.bikeCodeFault.getEditText().getText().toString())) {
                    Report r = createReport();
                    newReport(r);
                }
            }
        });
        binding.sendFixedReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIDBikeReport() & checkRackReport()){
                    sendFixReport();
                }
            }
        });


        binding.bikeCodeTextFault.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    binding.bikeCodeFault.setError(null);
                    binding.bikeCodeFault.setErrorEnabled(false);
                }
            }
        });
        binding.filledExposedDropdown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    binding.typeFaultReport.setError(null);
                    binding.typeFaultReport.setErrorEnabled(false);
                }
            }
        });
        binding.bikeCodeTextFixed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    binding.bikeCodeFixed.setError(null);
                    binding.bikeCodeFixed.setErrorEnabled(false);
                }
            }
        });
        binding.valoriRastrelliereFine.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    binding.posizioneBiciNuova.setError(null);
                    binding.posizioneBiciNuova.setErrorEnabled(false);
                }
            }
        });
    }

    public boolean checkType(String value){
        if(value.isEmpty()){
            binding.typeFaultReport.setError(getString(R.string.should_not_be_empty));
            binding.typeFaultReport.setErrorEnabled(true);
            binding.typeFaultReport.clearFocus();
            return false;
        }
        binding.typeFaultReport.setError(null);
        binding.typeFaultReport.setErrorEnabled(false);
        return true;
    }

    public boolean checkIdBike(String value){
        if(value.isEmpty()){
            binding.bikeCodeFault.setError(getString(R.string.should_not_be_empty));
            binding.bikeCodeFault.setErrorEnabled(true);
            binding.bikeCodeFault.clearFocus();
            return false;
        }
        binding.bikeCodeFault.setError(null);
        binding.bikeCodeFault.setErrorEnabled(false);
        return true;
    }
    private boolean checkRackReport() {
        if(binding.valoriRastrelliereFine.getText().length() == 0){
            binding.posizioneBiciNuova.setErrorEnabled(true);
            binding.posizioneBiciNuova.setError(getString(R.string.should_not_be_empty));
            binding.posizioneBiciNuova.clearFocus();
            return false;
        }
        binding.posizioneBiciNuova.setErrorEnabled(false);
        binding.posizioneBiciNuova.setError(null);
        return true;
    }

    private boolean checkIDBikeReport() {
        if(binding.bikeCodeTextFixed.getText().length() == 0){
            binding.bikeCodeFixed.setErrorEnabled(true);
            binding.bikeCodeFixed.setError(getString(R.string.should_not_be_empty));
            binding.bikeCodeFixed.clearFocus();
            return false;
        }
        binding.bikeCodeFixed.setError(null);
        binding.bikeCodeFixed.setErrorEnabled(false);
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
        final Observer<Resource<Report>> observer = new Observer<Resource<Report>>() {
            @Override
            public void onChanged(Resource<Report> report) {
                if(report.getStatusCode() == 200) {
                    DialogFragment newFragment = MyAlertDialogFragment.newInstance(getString(R.string.report_sent), getString(R.string.report_sent_body));

                    newFragment.show(getFragmentManager(), "dialog");
                    binding.bikeCodeFault.getEditText().setText("");
                    binding.typeFaultReport.getEditText().setText("");
                    binding.bikeFalutDesciption.getEditText().setText("");
                }else if(report.getStatusCode() == 404){
                    binding.bikeCodeFault.setErrorEnabled(true);
                    binding.bikeCodeFault.setError(getString(R.string.insert_vaild_value));
                    binding.bikeCodeFault.clearFocus();
                }
            }
        };
        reportMutableLiveDate = reportsViewModel.sendReport(getContext(), report);

        reportMutableLiveDate.observe(requireActivity(), observer);
    }

    private void checkCameraPermission(final int caller) {
        if (MyUtils.checkCameraPermission(getActivity())) {
            Intent intent = new Intent(getActivity().getApplicationContext(), QrReaderActivity.class);
            startActivityForResult(intent, caller);
        }else{
            MyUtils.showCameraPermissionDeniedDialog(getActivity(), getActivity().getSupportFragmentManager());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    int returnValue = data.getBundleExtra(Costants.DATA_DETECT).getInt(Costants.QR_CODE_DETECTION);
                    binding.bikeCodeTextFault.setText(String.valueOf(returnValue));
                }
                break;
            }
            case (1) :{
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    int returnValue = data.getBundleExtra(Costants.DATA_DETECT).getInt(Costants.QR_CODE_DETECTION);
                    binding.bikeCodeTextFixed.setText(String.valueOf(returnValue));
                }
                break;
            }
            case (2) :{
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    int returnValue = data.getBundleExtra(Costants.DATA_DETECT).getInt(Costants.QR_CODE_DETECTION);
                    binding.valoriRastrelliereFine.setText(String.valueOf(returnValue));
                }
                break;
            }
        }
    }
}


