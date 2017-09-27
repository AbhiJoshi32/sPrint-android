package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.TransactionFragmentListener;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.ui.adapter.FileAdapter;
import com.binktec.sprint.utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChooseFileFragment extends Fragment {

    private List<FileDetail> fragFileList = new ArrayList<>();
//    private String TAG = "Choose File Fragmetnt";

    private TransactionFragmentListener transactionFragmentListener;
    private FileAdapter fileAdapter;

    @BindView(R.id.file_recycler_view)
    RecyclerView fileRecyclerView;
    Unbinder unbinder;

    public ChooseFileFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionFragmentListener) {
            transactionFragmentListener = (TransactionFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthFragmentListener");
        }
    }

    public static ChooseFileFragment newInstance() {
        return new ChooseFileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose_file, container, false);
        unbinder = ButterKnife.bind(this, view);
        fileAdapter = new FileAdapter(fragFileList, new FileAdapter.MyAdapterListener() {
            @Override
            public void deleteRow(View v, int position) {
                transactionFragmentListener.removeFile(position);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        fileRecyclerView.setLayoutManager(mLayoutManager);
        fileRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fileRecyclerView.setAdapter(fileAdapter);
        RecyclerView.ItemDecoration  mDividerItemDecoration = new DividerItemDecoration(
                fileRecyclerView.getContext(),mLayoutManager.getOrientation()
        );
        fileRecyclerView.addItemDecoration(mDividerItemDecoration);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        transactionFragmentListener.removeFile(viewHolder.getAdapterPosition());

                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(fileRecyclerView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SessionManager.getFileDetail() != null) {
            fragFileList.clear();
            fragFileList.addAll(SessionManager.getFileDetail());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        transactionFragmentListener = null;
    }

    public void updateFileList(List<FileDetail> chosenFiles) {
        fragFileList.clear();
        fragFileList.addAll(chosenFiles);
        fileAdapter.notifyDataSetChanged();
    }

    public void clearFileList() {
        fragFileList.clear();
        fileAdapter.notifyDataSetChanged();
    }
}
