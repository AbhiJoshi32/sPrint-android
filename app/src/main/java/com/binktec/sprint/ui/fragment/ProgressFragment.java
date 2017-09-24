package com.binktec.sprint.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.PrintJobFragmentListener;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.ui.adapter.PrintJobListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProgressFragment extends Fragment {
    @BindView(R.id.progress_recycler_view)
    RecyclerView progressRecyclerView;
    Unbinder unbinder;
    private Handler progressFragHandler;

    private PrintJobFragmentListener printJobFragmentListener;
    private static final String TAG = "Progress Frag";

    private PrintJobListAdapter printJobListAdapter;
    private List<PrintJobDetail> printJobDetails = new ArrayList<>();

    public ProgressFragment() {
    }

    public static ProgressFragment newInstance() {
        return new ProgressFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressFragHandler = new Handler();
        printJobListAdapter = new PrintJobListAdapter(printJobDetails, new PrintJobListAdapter.PrintJobListListener() {
            @Override
            public void showPrintDetail(View v, int position) {
                printJobFragmentListener.printCardClicked();
            }

            @Override
            public void cancelPrintDetail(View v, final int position) {
                printJobFragmentListener.cancelUpload(printJobDetails.get(position));
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        progressRecyclerView.setLayoutManager(mLayoutManager);
        progressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        progressRecyclerView.setAdapter(printJobListAdapter);
        printJobFragmentListener.initProgressFragment();
        return view;
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

    public void insertProgressRecyclerView(final PrintJobDetail transactionDetail, final int i) {
        Log.d(TAG,"Item inserted");
        Log.d(TAG,"trnsacciton detail is "+transactionDetail+" index is "+i);
        Log.d(TAG,"printjobDetail is " + printJobDetails + "/n size is" + printJobDetails.size());
        progressRecyclerView.scrollToPosition(0);
        printJobDetails.add(i,transactionDetail);
        printJobListAdapter.notifyItemInserted(i);
    }

    public void changeProgressRecyclerView(PrintJobDetail changedTransaction, int changedIndex) {
        Log.d(TAG,"item changed");
        printJobDetails.set(changedIndex,changedTransaction);
        printJobListAdapter.notifyItemChanged(changedIndex);
    }

    public void removeProgressRecyclerView(int removeIndex) {
        printJobDetails.remove(removeIndex);
        printJobListAdapter.notifyItemRemoved(removeIndex);
    }

    public void initProgressRecyclerView(List<PrintJobDetail> progressPrintJobDetails) {
        printJobDetails.clear();
        Log.d(TAG,"Init called" + progressPrintJobDetails);
        progressRecyclerView.scrollToPosition(0);
        printJobDetails.addAll(progressPrintJobDetails);
        printJobListAdapter.notifyDataSetChanged();
    }
}