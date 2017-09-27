package com.binktec.sprint.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.PrintDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PrintDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.PaperTypeSpinner)
    Spinner PaperTypeSpinner;
    @BindView(R.id.ColourSpinner)
    Spinner ColourSpinner;
    @BindView(R.id.BindingSpinner)
    Spinner BindingSpinner;
    @BindView(R.id.LayoutSpinner)
    Spinner LayoutSpinner;
    @BindView(R.id.CopiesText)
    EditText CopiesText;
    @BindView(R.id.PagesSpinner)
    Spinner PagesSpinner;
    @BindView(R.id.PagesText)
    EditText PagesText;
    @BindView(R.id.PagesPerSheetSpinner)
    Spinner PagesPerSheetSpinner;
    Unbinder unbinder;

    public PrintDetailFragment() {
    }

    public static PrintDetailFragment newInstance() {
        return new PrintDetailFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        createSpinner(PagesSpinner, R.array.Pages);
        createSpinner(PaperTypeSpinner, R.array.PaperType);
        createSpinner(ColourSpinner, R.array.Colour);
        createSpinner(BindingSpinner, R.array.Binding);
        createSpinner(LayoutSpinner, R.array.Layout);
        createSpinner(PagesPerSheetSpinner, R.array.PagesPerSheet);
        CopiesText.setSelected(false);
        PagesText.setEnabled(false);
        return view;
    }

    public PrintDetail getSpninnerDetails() {
        PrintDetail printDetail = new PrintDetail();
        printDetail.setPrintColor(ColourSpinner.getSelectedItem().toString());
        printDetail.setBindingType(BindingSpinner.getSelectedItem().toString());
        printDetail.setPrintPaperType(PaperTypeSpinner.getSelectedItem().toString());
        printDetail.setPrintOrientation(LayoutSpinner.getSelectedItem().toString());
        printDetail.setPrintNoPages(PagesSpinner.getSelectedItem().toString());
        printDetail.setCopies(Integer.parseInt(CopiesText.getText().toString()));
        if (PagesPerSheetSpinner.getSelectedItem().equals("1 Sided")) {
            printDetail.setPagesPerSheet(1);
        } else {
            printDetail.setPagesPerSheet(2);
        }
        printDetail.setPagesText(PagesText.getText().toString());
        return printDetail;
    }

    public void createSpinner(Spinner spinner, int resId) {
        ArrayAdapter<CharSequence> Adapter = ArrayAdapter.createFromResource(getContext(),
                resId, android.R.layout.simple_spinner_item);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(Adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getSelectedItem().toString().compareTo("Custom") == 0) {
            PagesText.setEnabled(true);
        }
        if (adapterView.getSelectedItem().toString().compareTo("All") == 0) {
            PagesText.setText(null);
            PagesText.setEnabled(false);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showPagesTextError() {
        PagesText.setError("Invalid Input");
    }

    public void updatePrintDetailFragment(int size) {
        try {
            if (size == 1) {
                PagesSpinner.setEnabled(true);
                PagesSpinner.setSelection(0);
            } else {
                PagesSpinner.setEnabled(false);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
