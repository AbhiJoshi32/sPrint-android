package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.PrintJobFragmentListener;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.ui.adapter.HistoryJobListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment {

    @BindView(R.id.history_recycler_view)
    RecyclerView historyRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.historyImage)
    ImageView historyImage;
    @BindView(R.id.textView2)
    TextView noHistoryText;

    private PrintJobFragmentListener printJobFragmentListener;

    private HistoryJobListAdapter printJobListAdapter;
    private List<PrintJobDetail> printJobDetails = new ArrayList<>();

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        printJobListAdapter = new HistoryJobListAdapter(printJobDetails, new HistoryJobListAdapter.HistoryJobListListener() {
            @Override
            public void showPrintDetail(View v, int position) {
                printJobFragmentListener.printCardClicked(printJobDetails.get(position));
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(mLayoutManager);
        historyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        historyRecyclerView.setAdapter(printJobListAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        printJobFragmentListener.initHistoryFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PrintJobFragmentListener) {
            printJobFragmentListener = (PrintJobFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PrintJobFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        printJobFragmentListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void initHistoryRecyclerView(List<PrintJobDetail> historyPrintJobDetails) {
        historyRecyclerView.scrollToPosition(0);
        printJobDetails.clear();
        printJobDetails.addAll(historyPrintJobDetails);
        printJobListAdapter.notifyDataSetChanged();
        if (printJobDetails.size() > 0) {
            historyImage.setVisibility(View.GONE);
            noHistoryText.setVisibility(View.GONE);
        } else {
            historyImage.setVisibility(View.VISIBLE);
            noHistoryText.setVisibility(View.VISIBLE);
        }
    }

    public void addHistoryRecyclerView(PrintJobDetail historyDetail, int i) {
        historyRecyclerView.scrollToPosition(0);
        printJobDetails.add(i, historyDetail);
        printJobListAdapter.notifyItemInserted(i);
        historyImage.setVisibility(View.GONE);
        noHistoryText.setVisibility(View.GONE);
    }
}
