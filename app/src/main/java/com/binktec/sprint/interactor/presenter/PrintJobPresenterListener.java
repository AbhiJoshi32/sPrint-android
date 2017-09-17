package com.binktec.sprint.interactor.presenter;

import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public interface PrintJobPresenterListener {
    void openAuthActivity();

    void initializePrintJob(String displayName, String s);
    void updatePrintJobFragment(List<PrintJobDetail> printJobDetails);

    void updateHistoryJobFragment(List<PrintJobDetail> historyPrintJobDetail);

    void openInstructionActivity();
}
