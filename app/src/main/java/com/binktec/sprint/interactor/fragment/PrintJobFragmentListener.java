package com.binktec.sprint.interactor.fragment;

import com.binktec.sprint.modal.pojo.PrintJobDetail;

public interface PrintJobFragmentListener {
    void printCardClicked();

    void initProgressFragment();

    void cancelUpload(PrintJobDetail printJobDetail);

    void initHistoryFragment();
}
